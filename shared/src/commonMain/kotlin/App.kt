import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import core.getAppGradient
import root.DAGRootComponent
import root.RootContent

@Composable
fun App(rootComponent: DAGRootComponent) {
    MaterialTheme {
        Box(
            Modifier.fillMaxSize()
                .background(brush = getAppGradient())
        ) {
            RootContent(rootComponent)
        }
    }
}

expect fun getPlatformName(): String