package network

@kotlinx.serialization.Serializable
class RemoteUserResponse: BaseResponse<RemoteUser>()

@kotlinx.serialization.Serializable
data class RemoteUser(
    val id: String,
    val username: String,
    val isGuestUser: Boolean,
    val createdAt: String,
    val avatarColor: String,
)

@kotlinx.serialization.Serializable
data class User(
    val id: String,
    val userName: String,
    val avatarColor: String,
)