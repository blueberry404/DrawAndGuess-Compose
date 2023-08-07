package sockets

expect class PlatformSocket(url: String) {
    fun connectSocket(listener: PlatformSocketListener)
    fun closeSocket(code: Int, reason: String)
    fun sendMessage(msg: String)
    fun cleanup()
}