package createroom

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import core.extension.scopeCoroutine
import createroom.CreateRoomAction.ShowWaitingLobby
import createroom.CreateRoomIntent.CreateRoom
import createroom.CreateRoomIntent.OnRoomNameChanged
import createroom.CreateRoomIntent.OnRoomPasswordChanged
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

internal class CreateRoomViewModel(
    private val ioContext: CoroutineContext,
) : InstanceKeeper.Instance {

    private var _uiState = MutableStateFlow(CreateRoomState())
    val uiState: StateFlow<CreateRoomState> = _uiState.asStateFlow()

    private var _actions = Channel<CreateRoomAction>()
    val actions = _actions.receiveAsFlow()

    private val scope = CoroutineScope(ioContext + SupervisorJob())

    override fun onDestroy() {
        scope.cancel()
    }

    fun handleIntent(intent: CreateRoomIntent) {
        when (intent) {
            is OnRoomNameChanged -> _uiState.update { it.copy(roomName = intent.roomName) }
            is OnRoomPasswordChanged -> _uiState.update { it.copy(roomPassword = intent.password) }
            CreateRoom -> checkData()
        }
    }

    private fun checkData() {
        if (isValid()) {
            // TODO: After API call and room creation, open lobby
            performAction(ShowWaitingLobby("123"))
        } else {
            // TODO: Show dialog
        }
    }

    private fun performAction(action: CreateRoomAction) {
        scope.launch {
            _actions.send(action)
        }
    }

    private fun isValid(): Boolean {
        val state = _uiState.value
        return with(state) {
            roomName.length >= 8 && roomPassword.length >= 6
        }
    }
}