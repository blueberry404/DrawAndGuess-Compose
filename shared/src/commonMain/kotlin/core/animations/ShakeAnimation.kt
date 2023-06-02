package core.animations

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.keyframes

val shakeKeyframes: AnimationSpec<Float> = keyframes {
    durationMillis = 800
    val easing = LinearEasing

    // generate 8 keyframes
    for (i in 1..8) {
        val x = when (i % 3) {
            0 -> 14f
            1 -> -14f
            else -> 0f
        }
        x at durationMillis / 10 * i with easing
    }
}