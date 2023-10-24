package ui.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.popWhile
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import models.RoomContentMode
import ui.game.DefaultGameComponent
import ui.home.DefaultHomeComponent
import models.GameMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import ui.root.DAGRootComponent.DAGChild
import ui.root.DAGRootComponent.DAGChild.CreateRoomChild
import ui.root.DAGRootComponent.DAGChild.GameChild
import ui.root.DAGRootComponent.DAGChild.HomeChild
import ui.root.DAGRootComponent.DAGChild.WaitingRoomChild
import ui.root.DefaultDAGRootComponent.Config.CreateRoom
import ui.root.DefaultDAGRootComponent.Config.WaitingRoom
import ui.createroom.DefaultCreateRoomComponent
import ui.endgame.DefaultEndGameComponent
import ui.root.DAGRootComponent.DAGChild.EndGameChild
import ui.root.DefaultDAGRootComponent.Config.EndGame
import ui.root.DefaultDAGRootComponent.Config.Home
import ui.waitingroom.DefaultWaitingRoomComponent

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
            is Config.EndGame -> EndGameChild(getEndGameComponent(componentContext))
        }

    private fun getHomeComponent(componentContext: ComponentContext) =
        DefaultHomeComponent(componentContext, Dispatchers.IO) { gameMode, roomMode ->
            navigation.push(CreateRoom(gameMode, roomMode))
        }

    private fun getCreateRoomComponent(
        gameMode: GameMode,
        roomMode: RoomContentMode,
        componentContext: ComponentContext
    ) =
        DefaultCreateRoomComponent(componentContext, Dispatchers.Main, gameMode, roomMode, {
            navigation.push(WaitingRoom)
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
        DefaultGameComponent(componentContext, Dispatchers.Main) {
            navigation.push(Config.EndGame)
        }

    private fun getEndGameComponent(componentContext: ComponentContext) =
        DefaultEndGameComponent(componentContext) {
            navigation.popWhile { it != Home }
        }

    @Parcelize
    private sealed interface Config : Parcelable {
        object Home : Config
        data class CreateRoom(val gameMode: GameMode, val roomMode: RoomContentMode) : Config
        object WaitingRoom : Config
        object Game: Config
        object EndGame: Config
    }
}