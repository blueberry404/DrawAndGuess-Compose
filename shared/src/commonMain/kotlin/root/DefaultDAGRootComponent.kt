package root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import createroom.DefaultCreateRoomComponent
import home.DefaultHomeComponent
import root.DAGRootComponent.DAGChild
import root.DAGRootComponent.DAGChild.CreateRoomChild
import root.DAGRootComponent.DAGChild.HomeChild
import root.DAGRootComponent.DAGChild.WaitingRoomChild
import waitingroom.DefaultWaitingRoomComponent

class DefaultDAGRootComponent(
    componentContext: ComponentContext,
): DAGRootComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    private val _stack = childStack(
        source = navigation,
        initialConfiguration = Config.Home,
        handleBackButton = true,
        childFactory = ::child,
    )

    override val routerState: Value<ChildStack<*, DAGChild>>
        get() = _stack

    private fun child(config: Config, componentContext: ComponentContext): DAGChild =
        when (config) {
            is Config.Home -> HomeChild(getHomeComponent())
            is Config.CreateRoom -> CreateRoomChild(getCreateRoomComponent(componentContext))
            is Config.WaitingRoom -> WaitingRoomChild(getWaitingRoomComponent())
        }

    private fun getHomeComponent() =
        DefaultHomeComponent {
            navigation.push(Config.CreateRoom)
        }

    private fun getCreateRoomComponent(componentContext: ComponentContext) =
        DefaultCreateRoomComponent(componentContext) {
            navigation.pop()
        }

    private fun getWaitingRoomComponent() = DefaultWaitingRoomComponent {
        navigation.pop()
    }

    @Parcelize
    private sealed interface Config: Parcelable {
        object Home: Config
        object CreateRoom: Config
        object WaitingRoom: Config
    }
}