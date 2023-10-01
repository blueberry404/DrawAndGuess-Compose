package ui.game

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun TimerCountDown(
    tintColor: Color,
    progressColor: Color,
    backgroundColor: Color,
    initialValue: Float = 0f,
    timeInSeconds: Int = 0,
    onTimeFinished: () -> Unit,
) {
    val animatedSweepAngle = remember { Animatable(initialValue = initialValue) }

    LaunchedEffect(Unit) {
        animatedSweepAngle.animateTo(
            targetValue = 360f,
            animationSpec = tween(durationMillis = timeInSeconds * 1000, easing = EaseInOut)
        )
        onTimeFinished()
    }

    Box(Modifier.size(60.dp)) {
        Canvas(Modifier.fillMaxSize().padding(8.dp)) {

            val padding = size.width * 0.25f
            val radius = (size.width - padding) / 2
            val strokeWidth = 5.dp.toPx()
            drawCircle(
                color = tintColor,
                radius = radius,
                center = Offset(size.width / 2, size.height - radius),
                style = Stroke(width = strokeWidth)
            )

            val knobLeft = size.width / 3
            val knobHeight = 5.dp.toPx()

            drawRect(
                color = tintColor,
                topLeft = Offset(knobLeft, 0f),
                size = Size(knobLeft, knobHeight)
            )

            val knobBottomRectWidth = 4.dp.toPx()

            drawRect(
                color = tintColor,
                topLeft = Offset((size.width / 2) - (knobBottomRectWidth / 2), knobHeight),
                size = Size(knobBottomRectWidth, 5.dp.toPx())
            )

            drawCircle(
                color = tintColor,
                radius = 4.dp.toPx(),
                center = Offset(7.dp.toPx(), size.height * 0.28f)
            )

            drawCircle(
                color = tintColor,
                radius = 4.dp.toPx(),
                center = Offset(size.width - 7.dp.toPx(), size.height * 0.28f)
            )

            val backgroundRadius = radius - strokeWidth
            drawCircle(
                color = backgroundColor,
                radius = backgroundRadius,
                center = Offset((size.width / 2), size.height - backgroundRadius - strokeWidth)
            )

            drawArc(
                color = progressColor,
                startAngle = 270f,
                sweepAngle = animatedSweepAngle.value,
                useCenter = true,
                topLeft = Offset(padding, size.height - backgroundRadius * 2 - strokeWidth),
                size = Size(backgroundRadius * 2, backgroundRadius * 2)
            )
        }
    }
}