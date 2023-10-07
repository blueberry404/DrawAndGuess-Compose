package sockets

import core.GlobalData
import core.storage.KeyValueStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import models.User
import network.Constants
import network.Constants.ROOM_ID
import network.Constants.SOCKET_RECONNECTION
import network.Constants.USER_ID
import okhttp3.OkHttpClient
import okhttp3.WebSocket
import okhttp3.Request
import okhttp3.Response
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.IOException
import java.net.SocketException

actual class PlatformSocket actual constructor(url: String): KoinComponent {

    private val socketEndpoint = url
    private var webSocket: WebSocket? = null
    private var scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
    private var retryCount: Int = 0
    private val keyValueStorage: KeyValueStorage by inject()
    private var user: User? = keyValueStorage.user
    private var listener: PlatformSocketListener? = null

    actual fun connectSocket(listener: PlatformSocketListener) {
        this.listener = listener
        retryCount++

        val socketRequest = Request.Builder()
            .url(socketEndpoint)
            .addHeader(ROOM_ID, GlobalData.room.id)
            .addHeader(USER_ID, user?.id.orEmpty())
            .build()

        val webClient = OkHttpClient().newBuilder().build()
        webSocket = webClient.newWebSocket(
            socketRequest,
            object : okhttp3.WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    retryCount = 0
                    listener.onOpen()
                }
                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) = checkFailure(t)
                override fun onMessage(webSocket: WebSocket, text: String) = listener.onMessage(text)
                override fun onClosing(webSocket: WebSocket, code: Int, reason: String) = listener.onClosing(code, reason)
                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) = listener.onClosed(code, reason)
            }
        )
    }

    actual fun closeSocket(code: Int, reason: String) {
        webSocket?.close(code, reason)
        webSocket = null
    }

    actual fun sendMessage(msg: String) {
        webSocket?.send(msg)
    }

    actual fun cleanup() {
        webSocket?.cancel()
        webSocket = null
    }

    private fun checkFailure(t: Throwable) {
        cleanup()
        if (retryCount == Constants.RETRY_LIMIT) {
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
}