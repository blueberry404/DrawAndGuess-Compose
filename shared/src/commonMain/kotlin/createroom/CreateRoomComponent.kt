package createroom

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import core.extension.scopeCoroutine
import createroom.CreateRoomAction.ShowWaitingLobby
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

interface CreateRoomComponent {

    val uiState: StateFlow<CreateRoomState>
    fun onBackPressed()
    fun onIntent(intent: CreateRoomIntent)
}

class DefaultCreateRoomComponent(
    componentContext: ComponentContext,
    coroutineContext: CoroutineContext,
    private val showWaitingLobby: (String) -> Unit,
    private val popScreen: () -> Unit,
): CreateRoomComponent, ComponentContext by componentContext {

    private val viewModel: CreateRoomViewModel = instanceKeeper.getOrCreate {
        CreateRoomViewModel(coroutineContext)
    }
    override val uiState = viewModel.uiState

    private val scope = scopeCoroutine(coroutineContext)

    init {
        handleActions()
    }

    override fun onBackPressed() {
        popScreen()
    }

    override fun onIntent(intent: CreateRoomIntent) {
        viewModel.handleIntent(intent)
    }

    private fun handleActions() {
        scope.launch {
            viewModel.actions.collectLatest {
                when (it) {
                    is ShowWaitingLobby -> showWaitingLobby(it.roomId)
                    else -> {}
                }
            }
        }
    }
}