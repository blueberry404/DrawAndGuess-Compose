import androidx.compose.ui.window.ComposeUIViewController
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import platform.UIKit.UIViewController
import root.DAGRootComponent

actual fun getPlatformName(): String = "iOS"

fun debugLogs() {
    Napier.base(DebugAntilog())
}

fun MainViewController(rootComponent: DAGRootComponent): UIViewController =
    ComposeUIViewController {
        App(rootComponent)
    }