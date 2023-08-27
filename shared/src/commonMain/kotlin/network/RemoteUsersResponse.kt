package network

import kotlinx.serialization.Serializable

@Serializable
class RemoteUsersResponse: BaseResponse<List<RemoteRoomUser>>()

