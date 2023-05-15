package core

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

fun getAppGradient() = Brush.verticalGradient(
    colors = listOf(
        Color(Colors.APP_BACKGROUND_GRADIENT1),
        Color(Colors.APP_BACKGROUND_GRADIENT2)
    ))