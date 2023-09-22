package network

import core.extension.getInitials
import createroom.RoomStatus
import home.GameMode
import kotlinx.serialization.Serializable

@Serializable
class RemoteRoomResponse: BaseResponse<RemoteRoom>()

@Serializable
data class RemoteRoom(
    val id: String,
    val mode: String,
    val gameRounds: Int,
    val status: String,
    val users: List<RemoteRoomUser>,
    val userTurns: List<String>,
    val adminId: String,
    val name: String,
    val words: List<String>,
)

@Serializable
data class RemoteRoomUser(
    val id: String,
    val username: String,
    val avatarColor: String,
)

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