package ui.createroom

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import core.Colors


@Composable
fun RoomNameInputView(inputText: String = "", onTextChanged: (String) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = "Room Name",
            fontSize = 18.sp,
            color = Color(Colors.PRIMARY_TEXT)
        )
        InputView(Modifier.fillMaxWidth().weight(1f)) {
            RoomNameInput(inputText, 10, onTextChanged)
        }
    }
}

@Composable
fun RoomNameInput(
    inputText: String = "",
    maxChars: Int = Int.MAX_VALUE,
    onTextChanged: (String) -> Unit
) {
    TextField(
        value = inputText,
        onValueChange = { changed ->
            val finalStr = if (maxChars != Int.MAX_VALUE) changed.take(maxChars) else changed
            if(finalStr.all { it.isLetterOrDigit() }) {
                onTextChanged(finalStr)
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next,
        ),
        singleLine = true,
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color(Colors.PRIMARY_TEXT),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
    )
}
