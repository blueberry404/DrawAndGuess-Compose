package game

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
import core.Colors
import core.extension.toPx
import home.HomeHeader

@Composable
fun GameContent(component: GameComponent, modifier: Modifier) {
    val state by component.uiState.collectAsState()
    Box(modifier.fillMaxSize()) {
        Column(verticalArrangement = Arrangement.SpaceAround) {
            HomeHeader(modifier.fillMaxWidth().height(60.dp), 60.dp.toPx()) {
                GameHeader(state = state)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun GameHeader(modifier: Modifier = Modifier, state: GameState) {
    Row(modifier, horizontalArrangement = Arrangement.SpaceBetween) {
        TimerCountDown(
            Color(Colors.TIMER_TINT),
            Color(Colors.TIMER_PROGRESS),
            Color(Colors.TIMER_BACKGROUND),
            state.currentTime.toFloat(),
            state.totalTimeInSec
        )
        PlayersInfo(players = state.players)
    }
}