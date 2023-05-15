package core.extension

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@Composable
fun Dp.toPx() = LocalDensity.current.run { this@toPx.toPx() }


@Composable
fun Int.toDp() = LocalDensity.current.run { this@toDp.toDp() }