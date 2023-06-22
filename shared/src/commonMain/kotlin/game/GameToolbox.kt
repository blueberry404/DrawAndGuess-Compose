package game

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import core.Colors
import core.Images
import core.extension.toPx
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun GameToolbox(
    modifier: Modifier,
    onColorSelected: (Color) -> Unit,
    onStrokeWidthSelected: (Float) -> Unit,
    onUndo: () -> Unit,
    onErase: () -> Unit,
    onClear: () -> Unit,
) {
    Box(modifier.background(Color(Colors.KEYBOARD_BACKGROUND)).padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Image(
                    painter = painterResource(Images.UNDO),
                    null,
                    modifier = Modifier.clickable { onUndo() })
                Spacer(Modifier.height(8.dp))
                Image(
                    painter = painterResource(Images.ERASER),
                    null,
                    modifier = Modifier.clickable { onErase() })
                Spacer(Modifier.height(8.dp))
                Image(
                    painter = painterResource(Images.CLEAR),
                    null,
                    modifier = Modifier.clickable { onClear() })
            }
            ColorPalette(Modifier, onColorSelected)
            StrokePalette(Modifier.padding(8.dp), onStrokeWidthSelected)
        }
    }
}

@Composable
fun ColorPalette(modifier: Modifier, onColorSelected: (Color) -> Unit) {
    Column(modifier) {
        var row = 0
        while (row < 2) {
            Column(modifier) {
                Row(Modifier.padding(8.dp)) {
                    for (i in 0..4) {
                        val color = PAINT_COLORS[(5 * row) + i]
                        Box(Modifier.size(32.dp).background(
                            color = color,
                            shape = RoundedCornerShape(CornerSize(16.dp))
                        ).border(
                            1.dp,
                            Color(Colors.BORDER_PALETTE_COLOR),
                            shape = RoundedCornerShape(CornerSize(16.dp))
                        )
                            .clickable { onColorSelected(color) })
                        Spacer(Modifier.width(8.dp))
                    }
                    row++
                }
            }
        }
    }
}

@Composable
fun StrokePalette(modifier: Modifier, onStrokeWidthSelected: (Float) -> Unit) {
    val strokeWidths = remember {
        listOf(4.dp, 8.dp, 16.dp)
    }
    Column(modifier) {
        strokeWidths.forEach { dp ->
            val pixel = dp.toPx()
            Box(Modifier.size(36.dp).drawBehind {
                drawCircle(
                    color = Color(Colors.BORDER_PALETTE_COLOR),
                    radius = 18.dp.toPx(),
                    style = Stroke(
                        width = 2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(
                            floatArrayOf(
                                8.dp.toPx(),
                                4.dp.toPx()
                            )
                        )
                    ),
                )
                drawCircle(color = Color(Colors.BORDER_PALETTE_COLOR), radius = dp.toPx() / 2)
            }.clickable { onStrokeWidthSelected(pixel) })
            Spacer(Modifier.height(8.dp))
        }
    }
}

private val PAINT_COLORS = listOf(
    Color.Black,
    Color.Red,
    Color.Green,
    Color.Yellow,
    Color.Blue,
    Color(Colors.PAINT_PURPLE),
    Color(Colors.PAINT_OLIVE),
    Color.Cyan,
    Color.DarkGray,
    Color(Colors.PAINT_ORANGE)
)