package waitingroom

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import core.Colors
import core.Images
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@Composable
fun WaitingRoomList(modifier: Modifier, users: List<WaitingUser>) {
    LazyColumn(modifier) {
        items(users) {
            WaitingUserRow(Modifier.fillMaxWidth().height(64.dp), it)
        }
        item {
            WaitingOthersRow(Modifier.fillMaxWidth().height(64.dp))
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun WaitingUserRow(modifier: Modifier, user: WaitingUser) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Card(Modifier.clip(CircleShape).size(32.dp)) {
            Image(
                painterResource(Images.PLACEHOLDER),
                contentDescription = null,
                modifier = Modifier.fillMaxSize().padding(4.dp)
            )
        }
        Spacer(Modifier.width(16.dp))
        Text(
            text = user.name,
            style = MaterialTheme.typography.body1,
            color = Color(Colors.PRIMARY_TEXT)
        )
    }
}

@Composable
fun WaitingOthersRow(modifier: Modifier) {
    Row(
        modifier, verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(24.dp),
            color = Color(Colors.BACKGROUND_YELLOW),
            strokeCap = StrokeCap.Round
        )
        Spacer(Modifier.width(16.dp))
        Text(
            text = "Waiting for others...",
            style = MaterialTheme.typography.body1,
            color = Color.White
        )
    }
}