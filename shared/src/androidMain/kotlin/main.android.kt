import androidx.compose.runtime.Composable
import root.DAGRootComponent

actual fun getPlatformName(): String = "Android"

@Composable fun MainView(rootComponent: DAGRootComponent) = App(rootComponent)
