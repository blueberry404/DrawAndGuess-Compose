package ui.createroom

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import core.Colors


@Composable
fun RoomPasswordInputView(inputText: String = "", onTextChanged: (String) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = "Password",
            style = MaterialTheme.typography.subtitle2,
            color = Color(Colors.PRIMARY_TEXT)
        )
        InputView(Modifier.fillMaxWidth().weight(1f)) {
            RoomPasswordInput(inputText, 10, onTextChanged)
        }
    }
}

@Composable
fun RoomPasswordInput(inputText: String = "", maxChars: Int, onTextChanged: (String) -> Unit) {
    TextField(
        value = inputText,
        onValueChange = {
            val finalStr = if (maxChars != Int.MAX_VALUE) it.take(maxChars) else it
            onTextChanged(finalStr)
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done,
        ),
        singleLine = true,
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color(Colors.TEXT_COLOR_YELLOW),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        textStyle = MaterialTheme.typography.subtitle1.copy(textAlign = TextAlign.Center),
        visualTransformation = PasswordVisualTransformation()
    )
}