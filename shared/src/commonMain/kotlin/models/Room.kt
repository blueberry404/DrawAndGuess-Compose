package models

import core.extension.getInitials

data class Room(
    val id: String = "",
    val mode: GameMode = GameMode.None,
    val gameRounds: Int = 1,
    val status: RoomStatus = RoomStatus.Unknown,
    val users: List<RoomUser> = emptyList(),
    val userTurns: List<String> = emptyList(),
    val isAdmin: Boolean = false,
    val name: String = "",
    val words: List<String> = emptyList(),
)

data class RoomUser(
    val id: String = "",
    val username: String = "",
    val avatarColor: String = "#000000",
) {
    fun getInitials() = username.getInitials()
}