package network

import kotlinx.serialization.Serializable

@Serializable
data class UserRequest(val isGuestUser: Boolean = true)

@Serializable
data class CreateRoomRequest(
    val userId: String,
    val name: String,
    val password: String,
    val mode: String
)

@Serializable
data class JoinRoomRequest(
    val userId: String,
    val name: String,
    val password: String,
)
