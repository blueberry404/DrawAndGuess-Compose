package game


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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import core.Colors
import core.Images
import core.animations.shakeKeyframes
import core.extension.ifOnly
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@Composable
fun GameBodyContent(
    modifier: Modifier = Modifier,
    state: GameState,
    onKeyPressed: (Char) -> Unit,
    onAnimationCompleted: () -> Unit,
    onColorSelected: (Color) -> Unit,
    onStrokeWidthSelected: (Float) -> Unit,
    forceRestored: () -> Unit,
    onDragStarted: () -> Unit = {},
    onDragMoved: (Offset) -> Unit = {},
    onDragEnded: () -> Unit,
    onUndo: () -> Unit,
    onErase: () -> Unit,
    onClear: () -> Unit,
) {
    val drawingInfoState = mutableStateOf(state.drawingInfo)

    when {
        state.isCurrentUserChoosing -> GameContentCurrentUserChoosing(modifier, state)
        state.isOtherUserChoosing -> GameContentOtherUserChoosing(modifier, state)
        state.isCurrentUserDrawing -> GameContentCurrentUserDrawing(
            modifier,
            state,
            onColorSelected = {
                onColorSelected(it)
            },
            onStrokeWidthSelected = {
                onStrokeWidthSelected(it)
            },
            drawingInfo = drawingInfoState.value,
            forceRestored, onDragStarted, onDragMoved, onDragEnded,
            onUndo, onErase, onClear
        )

        state.isOtherUserDrawing -> GameContentOtherUserDrawing(
            modifier,
            state,
            onKeyPressed,
            onAnimationCompleted
        )

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
            text = "Time to draw:",
            fontSize = 24.sp,
            color = Color(Colors.PRIMARY_TEXT)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = state.word.actual.uppercase(),
            fontSize = 42.sp,
            fontWeight = FontWeight.Bold,
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
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(Colors.PRIMARY_TEXT)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "is looking at word",
            fontSize = 16.sp,
            color = Color(Colors.PRIMARY_TEXT)
        )
    }
}

@Composable
fun GameContentOtherUserDrawing(
    modifier: Modifier,
    state: GameState,
    onKeyPressed: (Char) -> Unit,
    onAnimationCompleted: () -> Unit,
) {
    Column(modifier) {
        DrawingCanvas(Modifier.fillMaxWidth().weight(1f), state)
        WordBlocks(Modifier.fillMaxWidth(), state.word, onAnimationCompleted)
        GameKeyboard(
            Modifier.fillMaxWidth().background(Color(Colors.KEYBOARD_BACKGROUND))
                .padding(vertical = 8.dp, horizontal = 4.dp), onKeyPressed
        )
    }
}

@Composable
fun GameContentCurrentUserDrawing(
    modifier: Modifier,
    state: GameState,
    onColorSelected: (Color) -> Unit,
    onStrokeWidthSelected: (Float) -> Unit,
    drawingInfo: DrawingInfo,
    forceRestored: () -> Unit,
    onDragStarted: () -> Unit = {},
    onDragMoved: (Offset) -> Unit = {},
    onDragEnded: () -> Unit,
    onUndo: () -> Unit,
    onErase: () -> Unit,
    onClear: () -> Unit,
) {
    Column(modifier) {
        DrawingCanvas(Modifier.fillMaxWidth().weight(1f), state, drawingInfo, forceRestored, onDragStarted, onDragMoved, onDragEnded)
        GameToolbox(
            Modifier.fillMaxWidth().wrapContentHeight(),
            onColorSelected,
            onStrokeWidthSelected,
            onUndo, onErase, onClear
        )
    }
}

@Composable
fun DrawingCanvas(
    modifier: Modifier,
    state: GameState,
    drawingInfo: DrawingInfo = DrawingInfo(),
    forceRestored: () -> Unit = {},
    onDragStarted: () -> Unit = {},
    onDragMoved: (Offset) -> Unit = {},
    onDragEnded: () -> Unit = {},
) {

    var pointsState = remember { mutableStateListOf<CanvasPolygon>() }
    val info = rememberUpdatedState(drawingInfo)

    LaunchedEffect(state.forceRestoreState) {
        if (state.forceRestoreState) {
            pointsState.clear()
            pointsState.addAll(state.polygons)
            forceRestored()
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
                    onDragStarted()
                }, onDragEnd = { onDragEnded() },
                    onDragCancel = {}, onDrag = { change, _ ->
                        pointsState.last().offsets.add(change.position)
                        onDragMoved(change.position)
                    })
            }
        }
    ) {
        Canvas(Modifier.fillMaxSize()) {
            pointsState.forEach { info ->
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
fun WordBlocks(modifier: Modifier, word: GameWord, onAnimationCompleted: () -> Unit) {
    BoxWithConstraints(modifier.padding(16.dp)) {
        val offsetX = remember { Animatable(0f) }

        LaunchedEffect(word.wiggle) {
            if (word.wiggle) {
                offsetX.animateTo(
                    targetValue = 0f,
                    animationSpec = shakeKeyframes,
                )
                onAnimationCompleted()
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
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(Colors.PRIMARY_TEXT),
                    )
                }
            }
        }
    }
}
