package network

import core.extension.getInitials
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

@Serializable
data class User(
    val id: String = "",
    val userName: String = "",
    val avatarColor: String = "#000000",
) {
    fun getInitials() = userName.getInitials()
}