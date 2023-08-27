package home

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import createroom.RoomContentMode
import createroom.RoomContentMode.Create
import createroom.RoomContentMode.Join
import home.GameMode.None
import kotlinx.coroutines.flow.StateFlow
import kotlin.coroutines.CoroutineContext

interface HomeComponent {

    val state: StateFlow<HomeState>
    fun onGameOptionSelected(gameMode: GameMode)
    fun joinRoom()
}

class DefaultHomeComponent(
    componentContext: ComponentContext,
    coroutineContext: CoroutineContext,
    private val onOptionSelected: (GameMode, RoomContentMode) -> Unit,
): HomeComponent, ComponentContext by componentContext {

    private val homeViewModel: HomeViewModel = instanceKeeper.getOrCreate {
        HomeViewModel(coroutineContext)
    }
    override val state: StateFlow<HomeState>
        get() = homeViewModel.state

    override fun onGameOptionSelected(gameMode: GameMode) {
        onOptionSelected(gameMode, Create)
    }

    override fun joinRoom() {
        onOptionSelected(None, Join)
    }
}