package home

import androidx.compose.ui.graphics.Color
import core.extension.getInitials

data class Player(
    val id: String,
    val name: String,
    val score: Int,
    val isDrawing: Boolean,
    val isCurrentUser: Boolean,
    val color: Color,
) {
    fun getDisplayName() = if (isCurrentUser) "You" else name
    fun getInitials() = name.getInitials()
}
