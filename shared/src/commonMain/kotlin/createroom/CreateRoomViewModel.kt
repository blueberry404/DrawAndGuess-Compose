package createroom

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import core.GlobalData
import createroom.CreateRoomAction.ShowWaitingLobby
import createroom.CreateRoomIntent.CreateRoom
import createroom.CreateRoomIntent.OnRoomNameChanged
import createroom.CreateRoomIntent.OnRoomPasswordChanged
import createroom.RoomContentMode.Create
import createroom.RoomContentMode.Join
import home.GameMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import network.DAGRepository
import network.Resource
import network.Resource.Error
import network.Resource.Success
import network.Room
import kotlin.coroutines.CoroutineContext

internal class CreateRoomViewModel(
    private val ioContext: CoroutineContext,
    private val gameMode: GameMode,
    private val roomMode: RoomContentMode
) : InstanceKeeper.Instance {

    private var _uiState = MutableStateFlow(
        CreateRoomState(
            buttonTitle = if (roomMode == Join) "Join Room" else "Create Room"
        )
    )
    val uiState: StateFlow<CreateRoomState> = _uiState.asStateFlow()

    private var _actions = Channel<CreateRoomAction>()
    val actions = _actions.receiveAsFlow()

    private val scope = CoroutineScope(ioContext + SupervisorJob())
    private val repository = DAGRepository()

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
            request()
        } else {
            // TODO: Show dialog
        }
    }

    private fun request() {
        when (roomMode) {
            Create -> requestRoom()
            else -> joinRoom()
        }
    }

    private fun requestRoom() {
        scope.launch {
            val state = _uiState.value
            val response =
                repository.createRoom(state.roomName, state.roomPassword, gameMode.toString())
            when (response) {
                is Success -> {
                    val room = response.data
                    GlobalData.room = room
                    connectSocket(room)
                }
                is Error -> {
                    // TODO: Show dialog
                }
            }
        }
    }

    private fun joinRoom() {
        scope.launch {
            val state = _uiState.value
            val response =
                repository.joinRoom(state.roomName, state.roomPassword)
            when (response) {
                is Success -> {
                    val room = response.data
                    GlobalData.room = room
                    connectSocket(room)
                }
                is Error -> {
                    // TODO: Show dialog
                }
            }
        }
    }

    private fun connectSocket(room: Room) {
        performAction(ShowWaitingLobby(room.id))
    }

    private fun performAction(action: CreateRoomAction) {
        scope.launch {
            _actions.send(action)
        }
    }

    private fun isValid(): Boolean {
        val state = _uiState.value
        return with(state) {
            roomName.length >= 6 && roomPassword.length >= 6
        }
    }
}