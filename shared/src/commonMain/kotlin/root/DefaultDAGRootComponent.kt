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
import createroom.RoomContentMode
import game.DefaultGameComponent
import home.DefaultHomeComponent
import home.GameMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import root.DAGRootComponent.DAGChild
import root.DAGRootComponent.DAGChild.CreateRoomChild
import root.DAGRootComponent.DAGChild.GameChild
import root.DAGRootComponent.DAGChild.HomeChild
import root.DAGRootComponent.DAGChild.WaitingRoomChild
import root.DefaultDAGRootComponent.Config.Game
import waitingroom.DefaultWaitingRoomComponent

class DefaultDAGRootComponent(
    componentContext: ComponentContext,
) : DAGRootComponent, ComponentContext by componentContext {

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
            is Config.CreateRoom -> CreateRoomChild(
                getCreateRoomComponent(
                    config.gameMode,
                    config.roomMode,
                    componentContext
                )
            )
            is Config.WaitingRoom -> WaitingRoomChild(
                getWaitingRoomComponent(
                    componentContext
                )
            )
            is Config.Game -> GameChild(getGameComponent(componentContext))
        }

    private fun getHomeComponent(componentContext: ComponentContext) =
        DefaultHomeComponent(componentContext, Dispatchers.IO) { gameMode, roomMode ->
            navigation.push(Config.CreateRoom(gameMode, roomMode))
        }

    private fun getCreateRoomComponent(
        gameMode: GameMode,
        roomMode: RoomContentMode,
        componentContext: ComponentContext
    ) =
        DefaultCreateRoomComponent(componentContext, Dispatchers.Main, gameMode, roomMode, {
            navigation.push(Config.WaitingRoom)
        }) {
            navigation.pop()
        }

    private fun getWaitingRoomComponent(componentContext: ComponentContext) =
        DefaultWaitingRoomComponent(componentContext, Dispatchers.Main, popScreen = {
            navigation.pop()
        }, openGame = {
            navigation.push(Config.Game)
        })

    private fun getGameComponent(componentContext: ComponentContext) =
        DefaultGameComponent(componentContext, Dispatchers.Main)

    @Parcelize
    private sealed interface Config : Parcelable {
        object Home : Config
        data class CreateRoom(val gameMode: GameMode, val roomMode: RoomContentMode) : Config
        object WaitingRoom : Config
        object Game: Config
    }
}