package game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import core.Colors
import core.extension.toDp
import core.extension.toPx


@Composable
fun GameBodyContent(modifier: Modifier = Modifier, state: GameState) =
    when {
        state.isCurrentUserChoosing -> {}
        state.isOtherUserChoosing -> GameContentOtherUserChoosing(modifier, state)
        state.isCurrentUserDrawing -> GameContentCurrentUserDrawing(modifier, state)
        state.isOtherUserDrawing -> GameContentOtherUserDrawing(modifier, state)
        else -> {}
    }

@Composable
fun GameContentOtherUserChoosing(modifier: Modifier = Modifier, state: GameState) {
    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            color = Color(Colors.BACKGROUND_YELLOW),
            modifier = Modifier.size(64.dp)
        )
        Spacer(Modifier.height(48.dp))
        Text(
            text = state.currentUsername,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(Colors.PRIMARY_TEXT)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "is choosing word",
            fontSize = 20.sp,
            color = Color(Colors.PRIMARY_TEXT)
        )
    }
}

@Composable
fun GameContentOtherUserDrawing(modifier: Modifier, state: GameState) {
    Column(modifier) {
        DrawingCanvas(Modifier.fillMaxWidth().weight(1f), state)
        WordBlocks(Modifier.fillMaxWidth(), "waterme", "APP")
        GameKeyboard(
            Modifier.fillMaxWidth().background(Color(Colors.KEYBOARD_BACKGROUND))
                .padding(vertical = 8.dp, horizontal = 4.dp)
        ) {}
    }
}

@Composable
fun GameContentCurrentUserDrawing(modifier: Modifier, state: GameState) {
    Column(modifier) {
        DrawingCanvas(Modifier.fillMaxWidth().weight(1f), state)
        GameToolbox(Modifier.fillMaxWidth().wrapContentHeight())
    }
}

@Composable
fun DrawingCanvas(modifier: Modifier, state: GameState) {

    val pointsState = remember { mutableStateListOf<MutableList<Offset>>() }

    Box(modifier.background(Color.White.copy(alpha = 0.5f))
        .padding(16.dp)
        .also {
            if (state.isCurrentUserDrawing) {
                it.pointerInput(Unit) {
                    detectDragGestures(onDragStart = {
                        pointsState.add(kotlin.collections.mutableListOf())
//                println("onDragStart:: $it")
                    }, onDragEnd = {
//                println("onDragEnd::")
                    }, onDragCancel = {
//                println("onDragCancel::")
                    }, onDrag = { change, _ ->
                        pointsState.last().add(change.position)
//                println(">>> pos: ${change.position} , uptime: ${change.uptimeMillis}, dragamount: $dragAmount")
                    })
                }
            }
        }
    ) {
        Canvas(Modifier.fillMaxSize()) {
            pointsState.forEach { list ->
                drawPoints(
                    list,
                    PointMode.Polygon,
                    Color.Red,
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

@Composable
fun WordBlocks(modifier: Modifier, actualWord: String, guessedWord: String) {
    BoxWithConstraints(modifier.padding(16.dp)) {
        val maxWidth = constraints.maxWidth
        val padding = 8.dp.toPx() * (actualWord.length - 1)
        val blockWidth = (maxWidth - padding) / actualWord.length
        val blockWidthInDp = blockWidth.toInt().toDp()

        LazyRow(modifier = Modifier.align(Alignment.Center)) {
            items(actualWord.length) { index ->
                Box(
                    modifier = Modifier
                        .sizeIn(maxWidth = blockWidthInDp, maxHeight = blockWidthInDp)
                        .aspectRatio(1f)
                        .padding(end = 4.dp)
                        .background(Color(Colors.GUESS_BOX), RoundedCornerShape(CornerSize(4.dp))),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (index > guessedWord.length - 1) "" else guessedWord[index].toString(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(Colors.PRIMARY_TEXT),
                    )
                }
            }
        }
    }
}
