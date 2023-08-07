package sockets

sealed interface SocketEvent {
    data class UserJoined(val message: String): SocketEvent
}