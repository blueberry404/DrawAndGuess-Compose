package core.exception

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(val error: String? = null)