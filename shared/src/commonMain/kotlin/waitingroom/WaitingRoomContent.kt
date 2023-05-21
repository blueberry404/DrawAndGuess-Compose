package waitingroom

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import core.Colors
import core.Images
import core.getAppGradient
import core.widgets.GameLogo
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun WaitingRoomContent(component: WaitingRoomComponent, modifier: Modifier) {
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
            LobbyHeader("Test")
            Spacer(Modifier.height(8.dp))
            WaitingRoomList(
                Modifier.fillMaxWidth().wrapContentHeight(), listOf(
                    WaitingUser("123", "Anum Amin"),
                    WaitingUser("123", "Another User"),
                    WaitingUser("123", "Abcdef Amin"),
                    WaitingUser("123", "Bcdef Amin"),
                )
            )
        }
    }
}

@Composable
fun LobbyHeader(name: String) {
    Column(
        Modifier.fillMaxWidth().height(48.dp).background(
            Color(Colors.BACKGROUND_YELLOW), RoundedCornerShape(CornerSize(8.dp))
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = "Waiting Lobby $name", style = MaterialTheme.typography.h6, color = Color.White)
    }
}