package network

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
)

@Serializable
data class RemoteRoomUser(
    val id: String,
    val username: String,
    val avatarColor: String,
)

data class Room(
    val id: String,
    val mode: GameMode,
    val gameRounds: Int,
    val status: RoomStatus,
    val users: List<RoomUser>,
    val userTurns: List<String>,
)

data class RoomUser(
    val id: String,
    val username: String,
    val avatarColor: String,
)