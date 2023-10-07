package sockets

import core.GlobalData
import core.storage.KeyValueStorage
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import models.User
import network.Constants.RETRY_LIMIT
import network.Constants.ROOM_ID
import network.Constants.SOCKET_RECONNECTION
import network.Constants.USER_ID
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import platform.Foundation.NSData
import platform.Foundation.NSError
import platform.Foundation.NSMutableURLRequest
import platform.Foundation.NSOperationQueue
import platform.Foundation.NSURL
import platform.Foundation.NSURLSession
import platform.Foundation.NSURLSessionConfiguration
import platform.Foundation.NSURLSessionWebSocketCloseCode
import platform.Foundation.NSURLSessionWebSocketCloseCodeAbnormalClosure
import platform.Foundation.NSURLSessionWebSocketDelegateProtocol
import platform.Foundation.NSURLSessionWebSocketMessage
import platform.Foundation.NSURLSessionWebSocketTask
import platform.Foundation.addValue
import platform.darwin.NSObject

actual class PlatformSocket actual constructor(url: String): KoinComponent {
    private val socketEndpoint = NSURL.URLWithString(url)!!
    private var webSocket: NSURLSessionWebSocketTask? = null
    private var listener: PlatformSocketListener? = null
    private var retryCount: Int = 0
    private var scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
    private val keyValueStorage: KeyValueStorage by inject()
    private var user: User? = keyValueStorage.user

    actual fun connectSocket(listener: PlatformSocketListener) {
        this.listener = listener
        retryCount++

        val urlSession = NSURLSession.sessionWithConfiguration(
            configuration = NSURLSessionConfiguration.defaultSessionConfiguration(),
            delegate = object : NSObject(), NSURLSessionWebSocketDelegateProtocol {
                override fun URLSession(
                    session: NSURLSession,
                    webSocketTask: NSURLSessionWebSocketTask,
                    didOpenWithProtocol: String?
                ) {
                    retryCount = 0
                    listener.onOpen()
                }
                override fun URLSession(
                    session: NSURLSession,
                    webSocketTask: NSURLSessionWebSocketTask,
                    didCloseWithCode: NSURLSessionWebSocketCloseCode,
                    reason: NSData?
                ) {
                    Napier.d { "URLSession didCloseWithCode called" }
                    listener.onClosed(didCloseWithCode.toInt(), reason.toString())
                }
            },
            delegateQueue = NSOperationQueue.currentQueue()
        )
        val request = NSMutableURLRequest(socketEndpoint)
        request.addValue(GlobalData.room.id, ROOM_ID)
        request.addValue(user?.id.orEmpty(), USER_ID)
        webSocket = urlSession.webSocketTaskWithRequest(request)

        listenMessages(listener)
        webSocket?.resume()
    }

    actual fun closeSocket(code: Int, reason: String) {
        webSocket?.cancelWithCloseCode(code.toLong(), null)
        webSocket = null
    }

    actual fun sendMessage(msg: String) {
        val message = NSURLSessionWebSocketMessage(msg)
        webSocket?.sendMessage(message) { err ->
            err?.let { println("send $msg error: $it") }
        }
    }

    actual fun cleanup() {
        webSocket?.cancel()
    }

    private fun listenMessages(listener: PlatformSocketListener) {

        webSocket?.receiveMessageWithCompletionHandler { message, nsError ->
            when {
                nsError != null -> {
                    if(!checkForFailure(nsError)) {
                        listener.onFailure(Throwable(nsError.description))
                    }
                }
                message != null -> {
                    message.string?.let { listener.onMessage(it) }
                }
            }
            listenMessages(listener)
        }
    }

    private fun checkForFailure(error: NSError?): Boolean =
        error?.let {
            return@let when (it.code.toInt()) {
                ECONNRESET, ENOTCONN, ETIMEDOUT -> {
                    closeAndReconnectSocket()
                    true
                }
                else -> false
            }
        } ?: false

    private fun closeAndReconnectSocket() {
        closeSocket(NSURLSessionWebSocketCloseCodeAbnormalClosure.toInt(), "Abnormal closure")
        if (retryCount == RETRY_LIMIT) {
            listener?.onFailure(Throwable("Connection Error"))
            return
        }
        scope.launch {
            delay(SOCKET_RECONNECTION)
            listener?.let {
                connectSocket(it)
            }
        }
    }

    private companion object {
        const val ECONNRESET = 54
        const val ENOTCONN = 57
        const val ETIMEDOUT = 60
    }
}