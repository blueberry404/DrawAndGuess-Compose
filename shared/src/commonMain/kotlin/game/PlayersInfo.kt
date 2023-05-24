package game

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import core.Colors
import core.Images
import home.Player
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@Composable
fun PlayersInfo(modifier: Modifier = Modifier, players: List<Player>) {
    Row(
        modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (player in players) {
            PlayerItem(Modifier.padding(horizontal = 8.dp), player)
        }
    }
}

@OptIn(ExperimentalResourceApi::class, ExperimentalFoundationApi::class)
@Composable
fun PlayerItem(modifier: Modifier = Modifier, player: Player) {
    Column(modifier.widthIn(max = 40.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Column(
            modifier = Modifier
                .drawBehind {
                    drawCircle(
                        Color.White, style = Stroke(
                            width = 2.dp.toPx(),
                            pathEffect =
                            if (player.isCurrentUser)
                                PathEffect.dashPathEffect(floatArrayOf(8.dp.toPx(), 4.dp.toPx()))
                            else
                                PathEffect.cornerPathEffect(16.dp.toPx())
                        )
                    )
                }.clip(CircleShape).size(32.dp).background(player.color),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (player.isDrawing) {
                Image(
                    painterResource(Images.PENCIL),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            } else {
                Text(
                    text = player.score.toString(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(Colors.PRIMARY_TEXT)
                )
            }
        }
        Spacer(Modifier.height(2.dp))
        Text(
            modifier = Modifier.basicMarquee(),
            text = player.getDisplayName(),
            fontSize = 12.sp,
            color = Color(Colors.PRIMARY_TEXT)
        )
    }
}