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
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
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
import sockets.SocketEvent
import sockets.SocketEventsListener
import sockets.SocketManager
import kotlin.coroutines.CoroutineContext

internal class CreateRoomViewModel(
    ioContext: CoroutineContext,
    private val gameMode: GameMode,
    private val roomMode: RoomContentMode
) : InstanceKeeper.Instance, SocketEventsListener {

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

    private var hasRoomCreated = true

    init {
        SocketManager.setListener(this)
    }

    override fun onDestroy() {
        SocketManager.removeListener()
        scope.cancel()
    }

    fun handleIntent(intent: CreateRoomIntent) {
        when (intent) {
            is OnRoomNameChanged -> {
                hasRoomCreated = false
                _uiState.update { it.copy(roomName = intent.roomName) }
            }
            is OnRoomPasswordChanged -> {
                hasRoomCreated = false
                _uiState.update { it.copy(roomPassword = intent.password) }
            }
            CreateRoom -> checkData()
        }
    }

    private fun checkData() {
        if (isValid()) {
            request()
        } else {
            showSnackbar("Both should be min 6 letters")
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
                    hasRoomCreated = true
                    connectSocket()
                }
                is Error -> {
                    showSnackbar(response.error)
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
                    hasRoomCreated = true
                    connectSocket()
                }
                is Error -> {
                    showSnackbar(response.error)
                }
            }
        }
    }

    private fun connectSocket() {
        SocketManager.connect()
    }

    private fun showSnackbar(message: String) {
        _uiState.update { it.copy(showSnackBar = true, errorMessage = message) }
        scope.launch {
            delay(2000)
            _uiState.update { it.copy(showSnackBar = false, errorMessage = "") }
        }
    }

    private fun performAction(action: CreateRoomAction) {
        scope.launch {
            _actions.send(action)
        }
    }

    private fun isValid(): Boolean {
        return with(_uiState.value) {
            roomName.length >= 6 && roomPassword.length >= 6
        }
    }

    override fun onConnected() {
        performAction(ShowWaitingLobby(GlobalData.room?.id.orEmpty()))
    }

    override fun onDisconnected() {
        Napier.e { "Disconnected in create room!" }
    }

    override fun onFailure(reason: String) {
        showSnackbar(reason)
    }

    override fun onEvent(event: SocketEvent) {

    }
}