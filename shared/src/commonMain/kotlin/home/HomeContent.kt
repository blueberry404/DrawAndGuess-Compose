@file:OptIn(ExperimentalResourceApi::class)

package home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import core.Colors
import core.Images
import core.animations.bounceClick
import core.extension.toPx
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@Composable
fun HomeContent(component: HomeComponent, modifier: Modifier) {
    Box(modifier.fillMaxSize()) {
        Column(verticalArrangement = Arrangement.SpaceAround) {
            HomeHeader(modifier.fillMaxWidth().height(60.dp), 60.dp.toPx()) {
                HeaderContent()
            }
            Spacer(modifier = Modifier.height(16.dp))
            GameLogo()
            Spacer(modifier = Modifier.height(40.dp))
            GameOptionsSection()
        }
    }
}

@Composable
fun HeaderContent() {
    Row(
        modifier = Modifier.padding(8.dp).fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(Modifier.clip(CircleShape).size(35.dp)) {
            Image(
                painterResource(Images.PLACEHOLDER),
                contentDescription = "Profile Image",
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text = "Welcome Guest 1024!",
            style = MaterialTheme.typography.body2,
            color = Color(Colors.PRIMARY_TEXT)
        )
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun GameLogo() {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painterResource(Images.LOGO),
            contentDescription = "Logo",
            modifier = Modifier.wrapContentSize(),
            contentScale = ContentScale.Inside
        )
    }
}

@Composable
fun GameOptionsSection() {
    Column {
        GameOptionsCard1()
        Spacer(modifier = Modifier.height(36.dp))
        GameOptionsCard2()
    }
}

@Composable
fun GameOptionsCard1() {
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
            .fillMaxWidth()
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
                        text = "Turn Based 1 on 1", style = MaterialTheme.typography.h6,
                        color = Color(Colors.PRIMARY_TEXT)
                    ) //#E9E0C9
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
fun GameOptionsCard2() {
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
            .fillMaxWidth()
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
                        text = "Play with Friends", style = MaterialTheme.typography.h6,
                        color = Color(Colors.PRIMARY_TEXT)
                    )
                    Text(
                        text = "Challenge friends for a quick match",
                        style = MaterialTheme.typography.body2,
                        color = Color(Colors.PRIMARY_TEXT)
                    )
                }
            }
        }
    }
}