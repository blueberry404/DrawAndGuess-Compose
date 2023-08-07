package sockets

interface SocketEventsListener {
    fun onConnected()
    fun onDisconnected()
    fun onFailure(reason: String)
    fun onEvent(event: SocketEvent)
}