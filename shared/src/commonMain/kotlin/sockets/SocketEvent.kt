package sockets

import game.CanvasState

sealed interface SocketEvent {
    data class UserJoined(val userIds: List<String>): SocketEvent
    data class UserLeft(val userIds: List<String>): SocketEvent
    data class RoomInfo(val userIds: List<String>): SocketEvent
    object PrepareForGame: SocketEvent
    object StartGame: SocketEvent
    data class SyncDrawing(val canvasState: CanvasState): SocketEvent
    object GameOver: SocketEvent
}