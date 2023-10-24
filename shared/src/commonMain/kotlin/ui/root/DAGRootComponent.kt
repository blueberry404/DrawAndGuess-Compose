package ui.root

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import ui.createroom.CreateRoomComponent
import ui.endgame.EndGameComponent
import ui.game.GameComponent
import ui.home.HomeComponent
import ui.waitingroom.WaitingRoomComponent

interface DAGRootComponent {

    val routerState: Value<ChildStack<*, DAGChild>>

    sealed class DAGChild {
        class HomeChild(val component: HomeComponent): DAGChild()
        class CreateRoomChild(val component: CreateRoomComponent): DAGChild()
        class WaitingRoomChild(val component: WaitingRoomComponent): DAGChild()
        class GameChild(val component: GameComponent): DAGChild()
        class EndGameChild(val component: EndGameComponent): DAGChild()
    }
}