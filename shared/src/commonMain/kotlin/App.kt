import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import root.DAGRootComponent
import root.RootContent

@Composable
fun App(rootComponent: DAGRootComponent) {
    MaterialTheme {
        Box(Modifier.fillMaxSize()) {
            RootContent(rootComponent)
        }
    }
}

expect fun getPlatformName(): String