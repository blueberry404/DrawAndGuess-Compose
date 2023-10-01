package models

import core.extension.getInitials
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String = "",
    val userName: String = "",
    val avatarColor: String = "#000000",
) {
    fun getInitials() = userName.getInitials()
}