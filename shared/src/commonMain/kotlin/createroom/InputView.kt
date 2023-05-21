package createroom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import core.Colors

@Composable
fun InputView(
    modifier: Modifier,
    child: @Composable () -> Unit,
) {
    Box(
        modifier
            .wrapContentHeight()
            .background(Color(Colors.INPUT_FIELD_BORDER), RoundedCornerShape(CornerSize(8.dp)))
            .padding(top = 4.dp)
            .background(Color(Colors.INPUT_FIELD_BACKGROUND), RoundedCornerShape(CornerSize(8.dp)))
    ) {
        child()
    }
}