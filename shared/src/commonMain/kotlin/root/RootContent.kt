package root

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.stackAnimation
import home.HomeContent
import root.DAGRootComponent.DAGChild.CreateRoomChild
import root.DAGRootComponent.DAGChild.HomeChild

@Composable
fun RootContent(component: DAGRootComponent, modifier: Modifier = Modifier) {
    Children(
        stack = component.routerState,
        modifier = modifier,
        animation = stackAnimation(fade() + scale()),
    ) {
        when (val child = it.instance) {
            is HomeChild -> HomeContent(child.component, Modifier)
            is CreateRoomChild -> CreateRoomChild(child.component)
        }
    }
}