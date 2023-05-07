import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController
import root.DAGRootComponent

actual fun getPlatformName(): String = "iOS"

fun MainViewController(rootComponent: DAGRootComponent): UIViewController =
    ComposeUIViewController {
        App(rootComponent)
    }