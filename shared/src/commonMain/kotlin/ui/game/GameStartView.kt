package ui.game

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun GameStartView(
    modifier: Modifier,
    strokeWidth: Float,
    colors: Array<Color>,
    textSize: TextUnit,
    animationCompleted: () -> Unit,
) {
    Box(modifier, contentAlignment = Alignment.Center) {
        val animatedSweepAngle = remember { Animatable(initialValue = 0f) }
        var backColor = remember { Color.Transparent }
        val currentColor = remember { mutableStateOf(0) }
        val number = remember { mutableStateOf(colors.size - 1) }
        val textDisplayed = remember {
            derivedStateOf {
                if (number.value < 1) "GO!" else number.value.toString()
            }
        }
        val next = remember {
            derivedStateOf {
                if (number.value < 1) "GO!" else (number.value - 1).toString()
            }
        }
        val goVisibleState = remember { MutableTransitionState(false) }

        LaunchedEffect(Unit) {
            repeat(colors.size) {
                animatedSweepAngle.animateTo(
                    targetValue = 360f,
                    animationSpec = tween(durationMillis = 1000)
                )
                animatedSweepAngle.snapTo(0f)
                backColor = colors[it]
                currentColor.value = currentColor.value + 1
                number.value = number.value - 1
                if (it == colors.size - 1) {
                    goVisibleState.targetState = true
                }
            }
            animationCompleted()
        }
        Canvas(Modifier.fillMaxSize().aspectRatio(1.0f).padding(8.dp)) {

            drawArc(
                color = backColor,
                startAngle = 270f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            )

            if (currentColor.value < colors.size) {
                drawArc(
                    color = colors[currentColor.value],
                    startAngle = 270f,
                    sweepAngle = animatedSweepAngle.value,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                )
            }
        }
        val index = if(currentColor.value == colors.size) colors.size - 1 else currentColor.value
            AnimatedContent(
                targetState = next.value,
                transitionSpec = {
                    slideInVertically { -it } with slideOutVertically { it }
                }
            ) {
                Text(
                    text = textDisplayed.value,
                    fontSize = textSize,
                    color = colors[index],
                    fontWeight = FontWeight.Bold,
                )
            }
    }
}