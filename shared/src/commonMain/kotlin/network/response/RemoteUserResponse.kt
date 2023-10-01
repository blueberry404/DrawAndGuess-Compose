package network.response

import kotlinx.serialization.Serializable

@Serializable
class RemoteUserResponse: BaseResponse<RemoteUser>()

@Serializable
data class RemoteUser(
    val id: String,
    val username: String,
    val isGuestUser: Boolean,
    val createdAt: String,
    val avatarColor: String,
)
