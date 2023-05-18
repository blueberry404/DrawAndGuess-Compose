package room

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextAlign.Companion
import androidx.compose.ui.unit.dp
import core.Colors
import core.animations.bounceClick

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