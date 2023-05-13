package home

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

fun getAppGradient() = Brush.verticalGradient(
    colors = listOf(
        Color(28, 49, 112),
        Color(20, 21, 59)
    ))