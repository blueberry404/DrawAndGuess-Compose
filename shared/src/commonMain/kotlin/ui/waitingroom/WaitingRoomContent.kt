package ui.waitingroom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import core.Colors
import core.getAppGradient
import core.widgets.DAGDialogView
import core.widgets.GameLogo

@Composable
fun WaitingRoomContent(component: WaitingRoomComponent, modifier: Modifier) {

    val state by component.uiState.collectAsState()

    Box(modifier = modifier.fillMaxSize().background(brush = getAppGradient())) {
        Column(
            modifier = modifier.fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(16.dp))
            GameLogo()
            Spacer(Modifier.height(40.dp))
            LobbyHeader(state.roomName)
            Spacer(Modifier.height(8.dp))
            WaitingRoomList(
                Modifier.fillMaxWidth().wrapContentHeight(),
                state.users
            )
        }
        DAGDialogView(
            modifier = Modifier.fillMaxSize(),
            info = state.dialogInfo,
            onPositiveClicked = {
                component.exitRoom()
            },
            onNegativeClicked = {
                component.dismissDialog()
            })
    }
}

@Composable
fun LobbyHeader(name: String) {
    Column(
        Modifier.fillMaxWidth().height(48.dp).background(
            Color(Colors.BACKGROUND_YELLOW)
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = name, style = MaterialTheme.typography.h6, color = Color.White)
    }
}