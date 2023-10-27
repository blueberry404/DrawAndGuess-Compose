package ui.waitingroom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Card
import androidx.compose.material.Icon
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
import core.Images
import core.getAppGradient
import core.widgets.DAGDialogView
import core.widgets.GameLogo
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@Composable
fun WaitingRoomContent(component: WaitingRoomComponent, modifier: Modifier) {

    val state by component.uiState.collectAsState()

    Box(modifier = modifier.fillMaxSize().background(brush = getAppGradient())) {
        Column(
            modifier = modifier.fillMaxSize().padding(top = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(16.dp))
            GameLogo()
            Spacer(Modifier.height(40.dp))
            LobbyHeader(state.roomName)
            Spacer(Modifier.height(8.dp))
            WaitingRoomList(
                Modifier.fillMaxWidth().wrapContentHeight().padding(horizontal = 32.dp),
                state.users
            )
            if (state.isMultiPlayer) {
                Spacer(Modifier.height(16.dp))
                WaitingInfoCard(Modifier.padding(horizontal = 32.dp))
            }
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

@OptIn(ExperimentalResourceApi::class)
@Composable
fun WaitingInfoCard(modifier: Modifier) {
    Card(
        modifier = modifier,
        backgroundColor = Color(Colors.BACKGROUND_WAITING_INFO),
        elevation = 4.dp,
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            Icon(
                painterResource(Images.INFO),
                contentDescription = "Information",
                modifier = Modifier.size(24.dp),
                tint = Color.White
            )
            Spacer(Modifier.width(16.dp))
            Text(
                text = "Game will start once 3 players join the room",
                style = MaterialTheme.typography.subtitle2,
                color = Color.White
            )
        }
    }
}