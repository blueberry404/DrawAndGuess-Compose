package createroom

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import createroom.CreateRoomIntent.CreateRoom
import createroom.CreateRoomIntent.OnRoomNameChanged
import createroom.CreateRoomIntent.OnRoomPasswordChanged

internal class CreateRoomViewModel/*(mainContext: CoroutineContext)*/ : InstanceKeeper.Instance {

    private var _uiState = MutableStateFlow(CreateRoomState())
    val uiState: StateFlow<CreateRoomState> = _uiState.asStateFlow()

    private var _actions = Channel<CreateRoomAction>()
    val actions = _actions.receiveAsFlow()

    override fun onDestroy() {

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

        }
        else {
            // TODO: Show dialog
        }
    }

    private fun isValid(): Boolean {
        val state = _uiState.value
        return with (state) {
            roomName.length >= 8 && roomPassword.length >= 6
        }
    }
}