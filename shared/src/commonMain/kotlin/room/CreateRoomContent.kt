package room

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import core.widgets.DAGButton
import core.widgets.GameLogo
import core.Images
import core.getAppGradient
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import room.CreateRoomIntent.CreateRoom
import room.CreateRoomIntent.OnRoomNameChanged
import room.CreateRoomIntent.OnRoomPasswordChanged

@OptIn(ExperimentalResourceApi::class)
@Composable
fun CreateRoomContent(component: CreateRoomComponent, modifier: Modifier) {

    val state by component.uiState.collectAsState()

    Column(modifier = modifier.fillMaxSize().background(brush = getAppGradient()).padding(32.dp)) {
        Row(horizontalArrangement = Arrangement.Start) {
            Image(
                painterResource(Images.BACK),
                contentDescription = "Back Button",
                modifier = Modifier.size(32.dp).clickable { component.onBackPressed() }
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround,
        ) {
            Spacer(Modifier.height(16.dp))
            GameLogo()
            Spacer(Modifier.height(40.dp))
            RoomNameInputView(inputText = state.roomName) {
                component.onIntent(OnRoomNameChanged(it))
            }
            Spacer(Modifier.height(32.dp))
            RoomPasswordInputView(inputText = state.roomPassword) {
                component.onIntent(OnRoomPasswordChanged(it))
            }
            Spacer(Modifier.height(40.dp))
            DAGButton(
                Modifier.width(150.dp).height(48.dp),
                title = "Create Room"
            ) {
                component.onIntent(CreateRoom)
            }
        }
    }
}
