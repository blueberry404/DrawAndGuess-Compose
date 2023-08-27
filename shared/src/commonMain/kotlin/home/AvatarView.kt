package home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
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

@Composable
fun AvatarView(modifier: Modifier, info: AvatarInfo) {
    Column(
        modifier = modifier
            .drawBehind {
                drawCircle(
                    Color.White, style = Stroke(
                        width = 2.dp.toPx(),
                        pathEffect = PathEffect.cornerPathEffect(16.dp.toPx())
                    )
                )
            }.clip(CircleShape).size(32.dp).background(info.color),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = info.initials,
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(Colors.PRIMARY_TEXT)
        )
    }
}