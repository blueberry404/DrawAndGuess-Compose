package root

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import createroom.CreateRoomComponent
import game.GameComponent
import home.HomeComponent
import waitingroom.WaitingRoomComponent

interface DAGRootComponent {

    val routerState: Value<ChildStack<*, DAGChild>>

    sealed class DAGChild {
        class HomeChild(val component: HomeComponent): DAGChild()
        class CreateRoomChild(val component: CreateRoomComponent): DAGChild()
        class WaitingRoomChild(val component: WaitingRoomComponent): DAGChild()
        class GameChild(val component: GameComponent): DAGChild()
    }
}