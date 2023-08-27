package waitingroom

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import kotlinx.coroutines.flow.StateFlow
import kotlin.coroutines.CoroutineContext

interface WaitingRoomComponent {

    val uiState: StateFlow<WaitingRoomState>
    fun onBackPressed()
    fun onGamersArrived()
}

class DefaultWaitingRoomComponent(
    componentContext: ComponentContext,
    coroutineContext: CoroutineContext,
    private val popScreen: () -> Unit,
): WaitingRoomComponent, ComponentContext by componentContext {

    private val viewModel: WaitingRoomViewModel = instanceKeeper.getOrCreate {
        WaitingRoomViewModel(coroutineContext)
    }
    override val uiState = viewModel.uiState

    override fun onBackPressed() {
        popScreen()
    }

    override fun onGamersArrived() {
        TODO("Not yet implemented")
    }
}