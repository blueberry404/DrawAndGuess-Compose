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
import extension.bounceClick
import extension.toPx
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
                painterResource("images/man.png"),
                contentDescription = "Profile Image",
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text = "Welcome Guest 1024!",
            style = MaterialTheme.typography.body2,
            color = Color(233, 234, 201)
        )
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun GameLogo() {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painterResource("images/logo.png"),
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
                    Color(8, 239, 255), //#08EFFF
                    Color(49, 156, 255), //#319CFF
                    Color(99, 124, 223), //#637CDF
                )
            ),
            shape = RoundedCornerShape(8.dp)
        )
            .fillMaxWidth()
    ) {
        Box(modifier = Modifier.wrapContentHeight()) {
            Image(
                painterResource("images/onetoone.png"),
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
                        color = Color(233, 234, 201)
                    ) //#E9E0C9
                    Text(
                        text = "Exchange doodle art with a friend",
                        style = MaterialTheme.typography.body2,
                        color = Color(233, 234, 201)
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
                    Color(255, 153, 211), //#FF99D3
                    Color(229, 105, 154), //#E5699A
                    Color(209, 77, 110), //D14D6E
                )
            ),
            shape = RoundedCornerShape(8.dp)
        )
            .fillMaxWidth()
    ) {
        Box(modifier = Modifier.wrapContentHeight()) {
            Image(
                painterResource("images/cup.png"),
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
                        color = Color(233, 234, 201)
                    )
                    Text(
                        text = "Challenge friends for a quick match",
                        style = MaterialTheme.typography.body2,
                        color = Color(233, 234, 201)
                    )
                }
            }
        }
    }
}