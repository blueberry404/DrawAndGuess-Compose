package ui.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import core.Colors
import core.extension.toPx
import ui.game.models.GameIntent.ClearCanvas
import ui.game.models.GameIntent.Erase
import ui.game.models.GameIntent.GameStart
import ui.game.models.GameIntent.OnDragEnded
import ui.game.models.GameIntent.OnDragMoved
import ui.game.models.GameIntent.OnDragStarted
import ui.game.models.GameIntent.SelectColor
import ui.game.models.GameIntent.SelectLetter
import ui.game.models.GameIntent.SelectStrokeWidth
import ui.game.models.GameIntent.StateRestoreCompleted
import ui.game.models.GameIntent.TimeOver
import ui.game.models.GameIntent.Undo
import ui.game.models.GameIntent.WiggleAnimationCompleted
import ui.game.models.GameState
import ui.home.HomeHeader

@Composable
fun GameContent(component: GameComponent, modifier: Modifier) {
    val state by component.uiState.collectAsState()
    Box(modifier.fillMaxSize()) {
        if (state.isDrawing) {
            HomeHeader(modifier.fillMaxWidth().height(60.dp).zIndex(2f), 60.dp.toPx()) {
                GameHeader(state = state) {
                    component.onIntent(TimeOver)
                }
            }
            Column(Modifier.zIndex(-1f)) {
                Spacer(Modifier.height(45.dp))
                GameBody(component, state)
            }
        }
        else {
            Column(verticalArrangement = Arrangement.SpaceAround) {
                HomeHeader(modifier.fillMaxWidth().height(60.dp), 60.dp.toPx()) {
                    GameHeader(state = state) {
                        component.onIntent(TimeOver)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                GameBody(component, state)
            }
        }
    }
}

@Composable
fun GameBody(component: GameComponent, state: GameState) {
    GameBodyContent(Modifier.fillMaxSize(), state, onKeyPressed = {
        component.onIntent(SelectLetter(it))
    }, onAnimationCompleted = {
        component.onIntent(WiggleAnimationCompleted)
    }, onColorSelected = {
        component.onIntent(SelectColor(it))
    }, onStrokeWidthSelected = {
        component.onIntent(SelectStrokeWidth(it))
    }, forceRestored = {
        component.onIntent(StateRestoreCompleted)
    }, onDragStarted = { component.onIntent(OnDragStarted) },
        onDragMoved = { component.onIntent(OnDragMoved(it)) },
        onDragEnded = { component.onIntent(OnDragEnded) },
    onUndo = { component.onIntent(Undo) },
    onClear = { component.onIntent(ClearCanvas) },
    onErase = { component.onIntent(Erase) },
    startGame = { component.onIntent(GameStart) })
}

@Composable
fun GameHeader(modifier: Modifier = Modifier, state: GameState, onTimeCompleted: () -> Unit) {
    Row(modifier, horizontalArrangement = Arrangement.SpaceBetween) {
        if (state.isDrawing) {
            TimerCountDown(
                Color(Colors.TIMER_TINT),
                Color(Colors.TIMER_PROGRESS),
                Color(Colors.TIMER_BACKGROUND),
                state.currentTime.toFloat(),
                state.totalTimeInSec,
                onTimeCompleted,
            )
            PlayersInfo(players = state.players)
        }
    }
}