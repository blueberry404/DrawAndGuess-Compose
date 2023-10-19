package core.widgets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import core.Colors.DIALOG_BACKGROUND
import core.Colors.DIALOG_DULL_BACKGROUND

@Composable
fun DAGDialogView(
    modifier: Modifier,
    info: DAGDialogInfo,
    onPositiveClicked: () -> Unit = {},
    onNegativeClicked: () -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    if (info.showDialog) {
        Box(
            modifier = modifier.background(Color(DIALOG_DULL_BACKGROUND))
                .clickable {
                    if (info.cancellable) {
                        onDismiss()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visible = info.showDialog,
                enter = slideInVertically { it },
                exit = slideOutVertically { it }
            ) {
                Card(backgroundColor = Color(DIALOG_BACKGROUND)) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val buttonModifier = Modifier.width(150.dp).height(48.dp)
                        if (info.title.isNotEmpty()) {
                            Text(
                                info.title,
                                color = Color.Black,
                                style = MaterialTheme.typography.h6,
                                fontSize = 16.sp
                            )
                            Spacer(Modifier.height(16.dp))
                        }
                        Text(info.message, color = Color.Black)
                        Spacer(Modifier.height(32.dp))
                        DAGButton(
                            modifier = buttonModifier,
                            title = info.buttonTitlePositive,
                            onClick = onPositiveClicked
                        )
                        if (info.buttonTitleNegative.isNotEmpty()) {
                            Spacer(Modifier.height(8.dp))
                            DAGButton(
                                modifier = buttonModifier,
                                title = info.buttonTitleNegative,
                                onClick = onNegativeClicked
                            )
                        }
                    }
                }
            }
        }
    }
}

data class DAGDialogInfo(
    val title: String = "",
    val message: String = "",
    val buttonTitlePositive: String = "",
    val buttonTitleNegative: String = "",
    val showDialog: Boolean = false,
    val cancellable: Boolean = false,
)