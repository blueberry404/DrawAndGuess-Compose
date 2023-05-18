package core.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import core.Colors
import core.animations.bounceClick

@Composable
fun DAGButton(modifier: Modifier, title: String, onClick: () -> Unit) {
    Box(
        modifier.bounceClick().background(
            color = Color(Colors.BUTTON_BOTTOM),
            shape = RoundedCornerShape(corner = CornerSize(8.dp))
        )
            .padding(bottom = 4.dp)
            .background(
                color = Color(Colors.BUTTON_BACKGROUND),
                shape = RoundedCornerShape(corner = CornerSize(8.dp))
            )
            .border(
                width = 1.dp,
                color = Color(Colors.BUTTON_BORDER),
                shape = RoundedCornerShape(corner = CornerSize(8.dp))
            ).clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Text(text = title, fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Bold)
    }
}