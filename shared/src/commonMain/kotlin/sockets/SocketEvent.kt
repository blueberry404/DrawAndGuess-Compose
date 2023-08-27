package sockets

sealed interface SocketEvent {
    data class UserJoined(val userIds: List<String>): SocketEvent
    data class UserLeft(val userIds: List<String>): SocketEvent
}