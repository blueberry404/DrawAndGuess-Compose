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
import home.DefaultHomeComponent
import room.CreateRoomComponent
import room.DefaultCreateRoomComponent
import root.DAGRootComponent.DAGChild
import root.DAGRootComponent.DAGChild.CreateRoomChild
import root.DAGRootComponent.DAGChild.HomeChild

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
            is Config.Home -> HomeChild(getHomeComponent(componentContext))
            is Config.CreateRoom -> CreateRoomChild(getCreateRoomComponent(componentContext))
        }

    private fun getHomeComponent(componentContext: ComponentContext) =
        DefaultHomeComponent(componentContext) {
            navigation.push(Config.CreateRoom)
        }

    private fun getCreateRoomComponent(componentContext: ComponentContext) =
        DefaultCreateRoomComponent(componentContext) {
            navigation.pop()
        }

    @Parcelize
    private sealed interface Config: Parcelable {
        object Home: Config
        object CreateRoom: Config
    }
}