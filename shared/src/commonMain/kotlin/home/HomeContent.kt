@file:OptIn(ExperimentalResourceApi::class)

package home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import core.Colors
import core.Images
import core.animations.bounceClick
import core.extension.toPx
import core.widgets.GameLogo
import home.GameMode.Many
import home.GameMode.Single
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@Composable
fun HomeContent(component: HomeComponent, modifier: Modifier) {
    val state by component.state.collectAsState()
    Box(modifier.fillMaxSize()) {
        Column(verticalArrangement = Arrangement.SpaceAround) {
            HomeHeader(modifier.fillMaxWidth().height(60.dp), 60.dp.toPx()) {
                state.user?.let { HeaderContent(it) }
            }
            Spacer(modifier = Modifier.height(16.dp))
            GameLogo()
            Spacer(modifier = Modifier.height(40.dp))
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Companion.Center) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(52.dp),
                        color = Color(Colors.BACKGROUND_YELLOW),
                        strokeCap = StrokeCap.Round
                    )
                }
            } else if (state.hasError()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Image(painterResource(Images.WARNING), "", Modifier.size(72.dp))
                    Spacer(Modifier.height(16.dp))
                    Text(
                        modifier = Modifier.padding(16.dp),
                        text = state.errorMessage.orEmpty(),
                        style = MaterialTheme.typography.h6,
                        color = Color(Colors.BACKGROUND_YELLOW),
                        textAlign = TextAlign.Center,
                    )
                }
            } else {
                GameOptionsSection(component::onGameOptionSelected, component::joinRoom)
            }
        }
    }
}

@Composable
fun HeaderContent(userInfo: HomeUserInfo) {
    Row(
        modifier = Modifier.padding(8.dp).fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AvatarView(Modifier.size(36.dp), userInfo.avatarInfo)
        Spacer(Modifier.width(8.dp))
        Text(
            text = userInfo.welcomeText,
            style = MaterialTheme.typography.body2,
            color = Color(Colors.PRIMARY_TEXT)
        )
    }
}

@Composable
fun GameOptionsSection(createRoom: (GameMode) -> Unit, joinRoomClicked: () -> Unit) {
    Column {
        GameOptionsCard1(createRoom)
        Spacer(modifier = Modifier.height(36.dp))
        GameOptionsCard2(createRoom)
        JoinTeamCard(joinRoomClicked)
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun GameOptionsCard1(createRoom: (GameMode) -> Unit) {
    Box(
        modifier = Modifier.bounceClick().padding(32.dp).background(
            brush = Brush.verticalGradient(
                listOf(
                    Color(Colors.HOME_CARD1_GRADIENT1),
                    Color(Colors.HOME_CARD1_GRADIENT2),
                    Color(Colors.HOME_CARD1_GRADIENT3),
                )
            ),
            shape = RoundedCornerShape(8.dp)
        )
            .fillMaxWidth().clickable { createRoom(Single) }
    ) {
        Box(modifier = Modifier.wrapContentHeight()) {
            Image(
                painterResource(Images.ONE_TO_ONE),
                contentDescription = null,
                modifier = Modifier.width(120.dp).aspectRatio(1.0f).align(Alignment.TopStart)
                    .offset(y = (-40).dp, x = 16.dp),
            )
            Row(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.weight(1f))
                Column(
                    modifier = Modifier.weight(2f).fillMaxWidth().padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Turn Based 1 on 1",
                        style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold),
                        color = Color(Colors.PRIMARY_TEXT)
                    )
                    Text(
                        text = "Exchange doodle art with a friend",
                        style = MaterialTheme.typography.body2,
                        color = Color(Colors.PRIMARY_TEXT)
                    )
                }
            }
        }
    }
}

@Composable
fun GameOptionsCard2(createRoom: (GameMode) -> Unit) {
    Box(
        modifier = Modifier.bounceClick().padding(32.dp).background(
            brush = Brush.verticalGradient(
                listOf(
                    Color(Colors.HOME_CARD2_GRADIENT1),
                    Color(Colors.HOME_CARD2_GRADIENT2),
                    Color(Colors.HOME_CARD2_GRADIENT3),
                )
            ),
            shape = RoundedCornerShape(8.dp)
        )
            .fillMaxWidth().clickable { createRoom(Many) }
    ) {
        Box(modifier = Modifier.wrapContentHeight()) {
            Image(
                painterResource(Images.CUP),
                contentDescription = null,
                modifier = Modifier.width(120.dp).aspectRatio(1.0f).align(Alignment.TopStart)
                    .offset(y = (-40).dp, x = 16.dp),
            )
            Row(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.weight(1f))
                Column(
                    modifier = Modifier.weight(2f).fillMaxWidth().padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Play with Friends",
                        style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold),
                        color = Color(Colors.PRIMARY_TEXT)
                    )
                    Text(
                        text = "Challenge upto 5 friends for a quick match",
                        style = MaterialTheme.typography.body2,
                        color = Color(Colors.PRIMARY_TEXT)
                    )
                }
            }
        }
    }
}

@Composable
fun JoinTeamCard(joinRoomClicked: () -> Unit) {
    Box(
        modifier = Modifier.bounceClick().padding(32.dp).background(
            brush = Brush.verticalGradient(
                listOf(
                    Color(Colors.HOME_JOIN_TEAM_GRADIENT1),
                    Color(Colors.HOME_JOIN_TEAM_GRADIENT2),
                )
            ),
            shape = RoundedCornerShape(8.dp)
        )
            .fillMaxWidth()
            .defaultMinSize(minHeight = 60.dp)
            .wrapContentHeight()
            .clickable { joinRoomClicked() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Have Code?", style = MaterialTheme.typography.subtitle1,
                color = Color(Colors.PRIMARY_TEXT)
            )
            Text(
                text = "Join Team",
                style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold),
                color = Color(Colors.PRIMARY_TEXT)
            )
        }
    }
}