package ui.endgame

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import core.Colors
import core.getAppGradient
import core.widgets.DAGButton

@Composable
fun EndGameContent(component: EndGameComponent, modifier: Modifier) {

    Box(modifier = modifier.fillMaxSize().background(brush = getAppGradient())) {
        Column(
            modifier = modifier.fillMaxSize().padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Thank you for playing!",
                style = MaterialTheme.typography.h4.copy(textAlign = TextAlign.Center),
                color = Color(Colors.TEXT_COLOR_YELLOW)
            )
            Spacer(Modifier.height(36.dp))
            Text(text = "\uD83D\uDE0A")
            Spacer(Modifier.height(36.dp))
            DAGButton(
                Modifier.width(150.dp).height(48.dp),
                title = "Take me Home",
                component::navigateToHome
            )
        }
    }
}