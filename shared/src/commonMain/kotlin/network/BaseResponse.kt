package network

@kotlinx.serialization.Serializable
open class BaseResponse<T>(val data: T? = null, val error: String? = null)