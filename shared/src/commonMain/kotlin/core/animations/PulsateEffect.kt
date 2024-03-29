package core.animations

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import core.animations.ButtonState.Idle
import core.animations.ButtonState.Pressed

fun Modifier.bounceClick(onClicked: () -> Unit) = composed {
    var buttonState by remember { mutableStateOf(Idle) }
    val scale by animateFloatAsState(if (buttonState == Pressed) 0.70f else 1f)

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = {  }
        )
        .pointerInput(onClicked) {
            awaitEachGesture {
                awaitFirstDown().also {
                    it.consume()
                    buttonState = Pressed
                }
                val up = waitForUpOrCancellation()
                if (up != null) {
                    up.consume()
                    buttonState = Idle
                    onClicked()
                }
            }
        }
}