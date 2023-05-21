package waitingroom

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.coroutines.CoroutineContext

class WaitingRoomViewModel(
    coroutineContext: CoroutineContext,
    private val roomId: String,
): InstanceKeeper.Instance {

    private var _uiState = MutableStateFlow(WaitingRoomState())
    val uiState: StateFlow<WaitingRoomState> = _uiState.asStateFlow()

    init {
        _uiState.value = WaitingRoomState(roomName = "Boom Boom", users = listOf(
            WaitingUser("123", "Anum Amin"),
            WaitingUser("123", "Another User"),
            WaitingUser("123", "Abcdef Amin"),
            WaitingUser("123", "Bcdef Amin"),
        ))
    }

    override fun onDestroy() {

    }
}