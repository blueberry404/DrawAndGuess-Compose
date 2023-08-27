package sockets

import kotlinx.serialization.Serializable

@Serializable
data class SocketMessage(val type: String, val payload: MessagePayload)

@Serializable
data class MessagePayload(
    val userId: String? = null,
    val roomId: String = "",
    val userIds: List<String>? = null,
    val error: String? = null
)