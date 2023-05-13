import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Brush.Companion
import androidx.compose.ui.graphics.Color
import home.getAppGradient
import root.DAGRootComponent
import root.RootContent

@Composable
fun App(rootComponent: DAGRootComponent) {
    MaterialTheme {
        Box(
            Modifier.fillMaxSize()
                .background(brush = getAppGradient())
//                .background(Color(229, 229, 229))
        ) {
            RootContent(rootComponent)
        }
    }
}

expect fun getPlatformName(): String