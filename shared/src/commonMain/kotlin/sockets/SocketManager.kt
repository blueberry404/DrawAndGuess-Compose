package sockets

import io.github.aakira.napier.Napier
import sockets.SocketEvent.UserJoined

object SocketManager: PlatformSocketListener {

    private const val URL = "http://192.168.1.107:3000"
    private val platformSocket = PlatformSocket(URL)
    private var listener: SocketEventsListener? = null

    fun setListener(listener: SocketEventsListener) {
        this.listener = listener
    }

    fun removeListener() {
        this.listener = null
    }

    fun connect() {
        platformSocket.connectSocket(this)
    }

    override fun onOpen() {
        Napier.e { "Connected!!!" }
        listener?.onConnected()
    }

    override fun onFailure(t: Throwable) {
        Napier.e { "Failure:: ${t.message}" }
        listener?.onFailure(t.message ?: "Some error occurred")
    }

    override fun onMessage(msg: String) {
        Napier.e { "Received a message!!! :: $msg" }
        listener?.onEvent(UserJoined(msg))
    }

    override fun onClosing(code: Int, reason: String) {
        Napier.e { "OnClosing::: code: $code, reason: $reason" }
    }

    override fun onClosed(code: Int, reason: String) {
        Napier.e { "Closed::: code: $code, reason: $reason" }
        listener?.onDisconnected()
    }
}