import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import kotlinx.cinterop.useContents
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.resource
import platform.UIKit.UIApplication
import platform.UIKit.UIWindow

actual fun getPaddingTopInset(): Int {
    val window = UIApplication.sharedApplication().windows.first() as UIWindow
    val top = window.safeAreaInsets.useContents { top }
    return top.toInt()
}

private val cache: MutableMap<String, Font> = mutableMapOf()
@OptIn(ExperimentalResourceApi::class)
@Composable
actual fun font(name: String, res: String, ext: String, weight: FontWeight, style: FontStyle): Font {
    return cache.getOrPut(res) {
        val byteArray = runBlocking {
            resource("$res.$ext").readBytes()
        }
        androidx.compose.ui.text.platform.Font(res, byteArray, weight, style)
    }
}