package home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import home.GameMode.Many
import home.GameMode.OneToOne

@Composable
fun HomeContent(component: HomeComponent, modifier: Modifier) {
    Column(modifier = modifier.fillMaxSize()) {
        Button(onClick = { component.onGameOptionSelected(OneToOne) }) {
            Text(text = "Invite your friend")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { component.onGameOptionSelected(Many) }) {
            Text(text = "Play with friends")
        }
    }
}