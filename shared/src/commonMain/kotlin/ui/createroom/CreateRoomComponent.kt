package ui.createroom

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleOwner
import com.arkivanov.essenty.lifecycle.subscribe
import core.GlobalData
import core.widgets.DAGDialogInfo
import ui.createroom.CreateRoomAction.ShowWaitingLobby
import ui.createroom.CreateRoomIntent.CreateRoom
import ui.createroom.CreateRoomIntent.OnRoomNameChanged
import ui.createroom.CreateRoomIntent.OnRoomPasswordChanged
import models.RoomContentMode.Create
import models.RoomContentMode.Join
import models.GameMode
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import models.RoomContentMode
import network.DAGRepository
import network.Resource.Error
import network.Resource.Success
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import sockets.SocketEvent
import sockets.SocketEvent.UserJoined
import sockets.SocketEventsListener
import sockets.SocketManager
import ui.createroom.CreateRoomIntent.DismissDialog
import kotlin.coroutines.CoroutineContext

interface CreateRoomComponent {

    val uiState: StateFlow<CreateRoomState>
    fun onBackPressed()
    fun onIntent(intent: CreateRoomIntent)
}

class DefaultCreateRoomComponent(
    componentContext: ComponentContext,
    coroutineContext: CoroutineContext,
    private val gameMode: GameMode,
    private val roomMode: RoomContentMode,
    private val showWaitingLobby: () -> Unit,
    private val popScreen: () -> Unit,
) : CreateRoomComponent, ComponentContext by componentContext, LifecycleOwner, SocketEventsListener,
    KoinComponent {

    private val scope = CoroutineScope(coroutineContext + SupervisorJob())

    private var _uiState = MutableStateFlow(
        CreateRoomState(
            buttonTitle = if (roomMode == Join) "Join Room" else "Create Room"
        )
    )

    override val uiState: StateFlow<CreateRoomState>
        get() = _uiState

    private var _actions = Channel<CreateRoomAction>()
    val actions = _actions.receiveAsFlow()

    private val repository: DAGRepository by inject()

    private var hasRoomCreated = true

    init {
        subscribeLifecycle()
        handleActions()
    }

    override fun onBackPressed() {
        popScreen()
    }

    override fun onIntent(intent: CreateRoomIntent) {
        when (intent) {
            is OnRoomNameChanged -> {
                hasRoomCreated = false
                _uiState.update { it.copy(roomName = intent.roomName) }
            }

            is OnRoomPasswordChanged -> {
                hasRoomCreated = false
                _uiState.update { it.copy(roomPassword = intent.password) }
            }

            DismissDialog -> {
                _uiState.update {
                    it.copy(dialogInfo = DAGDialogInfo(showDialog = false))
                }
            }
            CreateRoom -> checkData()
        }
    }

    private fun subscribeLifecycle() {
        lifecycle.subscribe(onPause = {
            SocketManager.removeListener()
        }, onResume = {
            SocketManager.setListener(this)
        }, onDestroy = {
            scope.cancel()
        })
    }

    private fun handleActions() {
        scope.launch {
            actions.collectLatest {
                when (it) {
                    ShowWaitingLobby -> showWaitingLobby()
                    else -> {}
                }
            }
        }
    }

    private fun checkData() {
        if (isValid()) {
            request()
        } else {
            showDialog("Both should be min 6 letters")
        }
    }

    private fun request() {
        if (hasRoomCreated) {
            postSocketConnection()
        } else {
            when (roomMode) {
                Create -> requestRoom()
                else -> joinRoom()
            }
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
                    showDialog(response.error)
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
                    showDialog(response.error)
                }
            }
        }
    }

    private fun connectSocket() {
        SocketManager.connect()
    }

    private fun showDialog(message: String) {
        _uiState.update {
            it.copy(
                dialogInfo = DAGDialogInfo(
                    message = message,
                    buttonTitlePositive = "OK",
                    showDialog = true,
                )
            )
        }
    }

    private fun performAction(action: CreateRoomAction) {
        scope.launch {
            _actions.send(action)
        }
    }

    private fun postSocketConnection() {
        hasRoomCreated = false
        repository.getCurrentUser()?.let { user ->
            if (roomMode == Create) {
                SocketManager.addDefaultRoomUser(user.id)
                SocketManager.requestForRoom(roomMode)
                performAction(ShowWaitingLobby)
            } else {
                SocketManager.requestForRoom(roomMode)
            }
        } ?: run {
            showDialog("Some error occurred")
        }
    }

    private fun isValid(): Boolean {
        return with(_uiState.value) {
            roomName.length >= 6 && roomPassword.length >= 6
        }
    }

    override fun onConnected() {
        postSocketConnection()
    }

    override fun onDisconnected() {
        Napier.e { "Disconnected in create room!" }
    }

    override fun onFailure(reason: String) {
        showDialog(reason)
    }

    override fun onEvent(event: SocketEvent) {
        when (event) {
            is UserJoined -> performAction(ShowWaitingLobby)
            else -> {}
        }
    }
}
