package ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import core.animations.keyPressEffect


val ROW1 = listOf('Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P')
val ROW2 = listOf('A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L')
val ROW3 = listOf('Z', 'X', 'C', 'V', 'B', 'N', 'M', '!')

@Composable
fun GameKeyboard(modifier: Modifier, onKeyPressed: (Char) -> Unit) {
    Column(modifier) {
        KeyboardRow(Modifier.fillMaxWidth(), ROW1, onKeyPressed)
        Spacer(Modifier.height(6.dp))
        KeyboardRow(Modifier.fillMaxWidth(), ROW2, onKeyPressed)
        Spacer(Modifier.height(6.dp))
        KeyboardRow(Modifier.fillMaxWidth(), ROW3, onKeyPressed)
    }
}

@Composable
fun KeyboardRow(modifier: Modifier, letters: List<Char>, onKeyPressed: (Char) -> Unit) {
    BoxWithConstraints(modifier) {
        LazyRow(modifier = Modifier.align(Alignment.Center)) {
            items(letters) {
                Box(
                    modifier = Modifier
                        .size(width = if (it == '!') 40.dp else 35.dp, height = 40.dp)
                        .keyPressEffect(it, onKeyPressed)
                        .padding(horizontal = 4.dp)
                        .background(
                            Color.White.copy(alpha = 0.7f),
                            RoundedCornerShape(CornerSize(4.dp))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (it == '!') "\u232B" else it.toString(),
                        style = MaterialTheme.typography.h6,
                    )
                }
            }
        }
    }
}