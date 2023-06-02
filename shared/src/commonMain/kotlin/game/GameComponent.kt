package game

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import kotlinx.coroutines.flow.StateFlow
import kotlin.coroutines.CoroutineContext

interface GameComponent {

    val uiState: StateFlow<GameState>

    fun onIntent(intent: GameIntent)
}

class DefaultGameComponent(
    componentContext: ComponentContext,
    coroutineContext: CoroutineContext,
): GameComponent, ComponentContext by componentContext {

    private val viewModel: GameViewModel = instanceKeeper.getOrCreate {
        GameViewModel(coroutineContext)
    }
    override val uiState: StateFlow<GameState>
        get() = viewModel.uiState

    override fun onIntent(intent: GameIntent) {
        viewModel.handleIntent(intent)
    }
}