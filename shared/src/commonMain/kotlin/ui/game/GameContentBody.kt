package ui.game


import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import core.Colors
import core.Images
import core.animations.shakeKeyframes
import core.extension.ifOnly
import core.extension.toPx
import ui.game.models.CanvasPolygon
import ui.game.models.DrawingInfo
import ui.game.models.GameState
import ui.game.models.GameWord
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import ui.game.models.GameIntent
import ui.game.models.GameIntent.OnDragEnded
import ui.game.models.GameIntent.OnDragMoved
import ui.game.models.GameIntent.OnDragStarted
import ui.game.models.GameIntent.WiggleAnimationCompleted

@Composable
fun GameBodyContent(
    modifier: Modifier = Modifier,
    state: GameState,
    callback: (GameIntent) -> Unit,
) {
    val drawingInfoState = mutableStateOf(state.drawingInfo)

    when {
        state.isCurrentUserChoosing -> GameContentCurrentUserChoosing(modifier, state)
        state.isOtherUserChoosing -> GameContentOtherUserChoosing(modifier, state)
        state.isCurrentUserDrawing -> GameContentCurrentUserDrawing(
            modifier, state, drawingInfoState.value, callback
        )

        state.isOtherUserDrawing -> GameContentOtherUserDrawing(
            modifier,
            state,
            callback
        )
        state.isStarting -> GameStartViewContainer(Modifier.fillMaxSize(), callback)
        state.isTimeOver || state.isGameOver -> GameOverViewContainer(Modifier.fillMaxSize(), state)
        else -> {}
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun GameContentCurrentUserChoosing(modifier: Modifier = Modifier, state: GameState) {
    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(painterResource(Images.PENCIL), "", Modifier.size(48.dp))
        Spacer(Modifier.height(48.dp))
        Text(
            text = "Time to draw",
            style = MaterialTheme.typography.h5,
            color = Color(Colors.PRIMARY_TEXT)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = state.word.actual.uppercase(),
            style = MaterialTheme.typography.h3,
            color = Color(Colors.BACKGROUND_YELLOW)
        )
    }
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
            style = MaterialTheme.typography.h4,
            color = Color(Colors.PRIMARY_TEXT)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "is thinking about word",
            style = MaterialTheme.typography.subtitle1,
            color = Color(Colors.PRIMARY_TEXT)
        )
    }
}

@Composable
fun GameContentOtherUserDrawing(
    modifier: Modifier,
    state: GameState,
    callback: (GameIntent) -> Unit,
) {
    Column(modifier) {
        DrawingCanvas(Modifier.fillMaxWidth().weight(1f), state)
        WordBlocks(Modifier.fillMaxWidth(), state.word, callback)
        GameKeyboard(
            Modifier.fillMaxWidth().background(Color(Colors.KEYBOARD_BACKGROUND))
                .padding(vertical = 8.dp, horizontal = 4.dp), callback
        )
    }
}

@Composable
fun GameContentCurrentUserDrawing(
    modifier: Modifier,
    state: GameState,
    drawingInfo: DrawingInfo,
    callback: (GameIntent) -> Unit,
) {
    Column(modifier) {
        DrawingCanvas(Modifier.fillMaxWidth().weight(1f), state, drawingInfo, callback)
        GameToolbox(Modifier.fillMaxWidth().wrapContentHeight(), callback)
    }
}

@Composable
fun DrawingCanvas(
    modifier: Modifier,
    state: GameState,
    drawingInfo: DrawingInfo = DrawingInfo(),
    callback: (GameIntent) -> Unit = {},
) {

    var pointsState = remember { mutableStateListOf<CanvasPolygon>() }
    val info = rememberUpdatedState(drawingInfo)

    if (state.isCurrentUserDrawing) {
        LaunchedEffect(state.forceRestoreState) {
            if (state.forceRestoreState) {
                pointsState.clear()
                pointsState.addAll(state.polygons)
                callback(GameIntent.StateRestoreCompleted)
            }
        }
    }

    Box(modifier.background(Color.White.copy(alpha = 0.5f))
        .padding(16.dp)
        .ifOnly(state.isCurrentUserDrawing) {
            Modifier.pointerInput(Unit) {
                detectDragGestures(onDragStart = {
                    val drawingState = CanvasPolygon(
                        strokeWidth = info.value.strokeWidth,
                        paintColor = info.value.paintColor
                    )
                    pointsState.add(drawingState)
                    callback(OnDragStarted)
                }, onDragEnd = { callback(OnDragEnded) },
                    onDragCancel = {}, onDrag = { change, _ ->
                        pointsState.last().offsets.add(change.position)
                        callback(OnDragMoved(change.position))
                    })
            }
        }
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val list = if (state.isCurrentUserDrawing) pointsState else state.polygons
            list.forEach { info ->
                drawPoints(
                    points = info.offsets,
                    pointMode = PointMode.Polygon,
                    color = info.paintColor,
                    strokeWidth = info.strokeWidth,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

@Composable
fun WordBlocks(modifier: Modifier, word: GameWord, callback: (GameIntent) -> Unit) {
    BoxWithConstraints(modifier.padding(16.dp)) {
        val offsetX = remember { Animatable(0f) }

        LaunchedEffect(word.wiggle) {
            if (word.wiggle) {
                offsetX.animateTo(
                    targetValue = 0f,
                    animationSpec = shakeKeyframes,
                )
                callback(WiggleAnimationCompleted)
            }
        }

        LazyRow(
            modifier = Modifier.align(Alignment.Center)
                .offset(x = offsetX.value.dp)
        ) {
            items(word.actual.length) { index ->
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .padding(end = 4.dp)
                        .background(Color(Colors.GUESS_BOX), RoundedCornerShape(CornerSize(4.dp))),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (index > word.guessed.length - 1) "" else word.guessed[index].toString(),
                        style = MaterialTheme.typography.h5,
                        fontWeight = FontWeight.Bold,
                        color = Color(Colors.PRIMARY_TEXT),
                    )
                }
            }
        }
    }
}

@Composable
fun GameStartViewContainer(modifier: Modifier, callback: (GameIntent) -> Unit) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        GameStartView(
            modifier = Modifier.size(200.dp),
            strokeWidth = 16.dp.toPx(),
            colors = arrayOf(
                Color(Colors.COUNTDOWN_COLOR_1),
                Color(Colors.COUNTDOWN_COLOR_2),
                Color(Colors.COUNTDOWN_COLOR_3),
                Color(Colors.COUNTDOWN_COLOR_4)
            ),
            textSize = 64.sp,
        ) { callback(GameIntent.GameStart) }
    }
}

@Composable
fun GameOverViewContainer(modifier: Modifier, state: GameState) {
    Column(
        modifier = modifier.padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            state.gameOverMessage,
            color = Color(Colors.TEXT_COLOR_YELLOW),
            style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(64.dp))
        Text(
            "The word was",
            style = MaterialTheme.typography.subtitle1,
            fontSize = 18.sp,
            color = Color.White
        )
        Spacer(Modifier.height(8.dp))
        Text(
            state.word.actual,
            style = MaterialTheme.typography.h3,
            color = Color.White
        )
    }
}