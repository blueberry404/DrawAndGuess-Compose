package ui.root

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.stackAnimation
import ui.createroom.CreateRoomContent
import ui.endgame.EndGameContent
import ui.game.GameContent
import ui.home.HomeContent
import ui.root.DAGRootComponent.DAGChild.CreateRoomChild
import ui.root.DAGRootComponent.DAGChild.EndGameChild
import ui.root.DAGRootComponent.DAGChild.GameChild
import ui.root.DAGRootComponent.DAGChild.HomeChild
import ui.root.DAGRootComponent.DAGChild.WaitingRoomChild
import ui.waitingroom.WaitingRoomContent

@Composable
fun RootContent(component: DAGRootComponent, modifier: Modifier = Modifier) {
    Children(
        stack = component.routerState,
        modifier = modifier,
        animation = stackAnimation(fade() + scale()),
    ) {
        when (val child = it.instance) {
            is HomeChild -> HomeContent(child.component, Modifier)
            is CreateRoomChild -> CreateRoomContent(child.component, Modifier)
            is WaitingRoomChild -> WaitingRoomContent(child.component, Modifier)
            is GameChild -> GameContent(child.component, Modifier)
            is EndGameChild -> EndGameContent(child.component, Modifier)
        }
    }
}