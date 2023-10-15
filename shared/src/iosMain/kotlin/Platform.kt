import kotlinx.cinterop.useContents
import platform.UIKit.UIApplication
import platform.UIKit.UIWindow

actual fun getPaddingTopInset(): Int {
    val window = UIApplication.sharedApplication().windows.first() as UIWindow
    val top = window.safeAreaInsets.useContents { top }
    return top.toInt()
}