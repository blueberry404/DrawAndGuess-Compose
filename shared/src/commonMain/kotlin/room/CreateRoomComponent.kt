package room

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import kotlinx.coroutines.flow.StateFlow

interface CreateRoomComponent {

    val uiState: StateFlow<CreateRoomState>
    fun onBackPressed()
    fun onIntent(intent: CreateRoomIntent)
}

class DefaultCreateRoomComponent(
    componentContext: ComponentContext,
    private val popScreen: () -> Unit,
): CreateRoomComponent, ComponentContext by componentContext {

    private val viewModel: CreateRoomViewModel = instanceKeeper.getOrCreate {
        CreateRoomViewModel()
    }
    override val uiState = viewModel.uiState

    override fun onBackPressed() {
        popScreen()
    }

    override fun onIntent(intent: CreateRoomIntent) {
        viewModel.handleIntent(intent)
    }
}