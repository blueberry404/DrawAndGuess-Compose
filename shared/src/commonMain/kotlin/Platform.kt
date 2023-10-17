import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight

expect fun getPaddingTopInset(): Int

@Composable
expect fun font(name: String, res: String, ext: String, weight: FontWeight, style: FontStyle): Font