package network.response

import kotlinx.serialization.Serializable
import network.response.BaseResponse
import network.response.RemoteRoomUser

@Serializable
class RemoteUsersResponse: BaseResponse<List<RemoteRoomUser>>()

