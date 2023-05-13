package home

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

@Composable
fun HomeHeader(modifier: Modifier, height: Float, content: @Composable () -> Unit) {
    Box(modifier.border(
        width = 2.dp, color = Color(77, 117, 205),
        shape = HeaderShape(height)
    )
        .drawBehind {
            scale(0.9f) {
                drawPath(path = headerPath(size = size, height = height),
                color = Color(24, 40, 81))
            }
        }
    ) { content() }
}

class HeaderShape(private val height: Float) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline = Outline.Generic(
        path = headerPath(size, height)
    )
}

private fun headerPath(size: Size, height: Float): Path {
    return Path().apply {
        reset()
        val offScreen = (size.width) * 0.5f
        arcTo(
            Rect(
                left = -offScreen,
                top = height,
                right = size.width + offScreen,
                bottom = size.width * 0.5f
            ),
            startAngleDegrees = 180.0f,
            sweepAngleDegrees = 180.0f,
            forceMoveTo = false,
        )
        lineTo(size.width + offScreen + 1, -10f)
        lineTo(-offScreen, -10f)
        close()
    }
}