package network.response

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
