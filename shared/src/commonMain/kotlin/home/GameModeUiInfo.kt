package home

import androidx.compose.ui.graphics.Color

data class GameModeUiInfo(
    val title: String,
    val description: String,
    val image: String,
    val gradient: List<Color>,
)