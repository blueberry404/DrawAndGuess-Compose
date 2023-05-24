package core.extension

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Dp.toPx() = LocalDensity.current.run { this@toPx.toPx() }

@Composable
fun Int.toDp(): Dp {
    val density = LocalDensity.current.density
    return (this / density).dp
}