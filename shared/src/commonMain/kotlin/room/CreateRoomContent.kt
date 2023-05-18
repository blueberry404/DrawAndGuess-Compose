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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import core.widgets.DAGButton
import core.widgets.GameLogo
import core.Images
import core.getAppGradient
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun CreateRoomContent(component: CreateRoomComponent, modifier: Modifier) {
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
            RoomNameInputView { }
            Spacer(Modifier.height(32.dp))
            RoomPasswordInputView { }
            Spacer(Modifier.height(40.dp))
            DAGButton(
                Modifier.width(150.dp).height(48.dp),
                title = "Create Room"
            ) {

            }
        }
    }
}
