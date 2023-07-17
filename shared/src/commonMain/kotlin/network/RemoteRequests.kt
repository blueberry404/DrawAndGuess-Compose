package network

import kotlinx.serialization.Serializable

@Serializable
data class UserRequest(val isGuestUser: Boolean = true)
