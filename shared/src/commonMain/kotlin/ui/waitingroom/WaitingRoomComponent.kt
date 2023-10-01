package ui.waitingroom

import androidx.compose.ui.graphics.Color
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.subscribe
import core.GlobalData
import models.AvatarInfo
import models.GameMode.Many
import models.GameMode.Single
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import network.DAGRepository
import network.Resource
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import sockets.SocketEvent
import sockets.SocketEvent.PrepareForGame
import sockets.SocketEvent.RoomInfo
import sockets.SocketEvent.UserJoined
import sockets.SocketEvent.UserLeft
import sockets.SocketEventsListener
import sockets.SocketManager
import sockets.SocketManager.KEY_PREPARE_GAME
import kotlin.coroutines.CoroutineContext

interface WaitingRoomComponent {

    val uiState: StateFlow<WaitingRoomState>
    fun onBackPressed()
}

class DefaultWaitingRoomComponent(
    componentContext: ComponentContext,
    coroutineContext: CoroutineContext,
    private val popScreen: () -> Unit,
    private val openGame: () -> Unit,
): WaitingRoomComponent, ComponentContext by componentContext, SocketEventsListener, KoinComponent {

    private var _uiState = MutableStateFlow(WaitingRoomState())
    override val uiState = _uiState.asStateFlow()

    private val room = GlobalData.room
    private val repository: DAGRepository by inject()

    private val scope = CoroutineScope(coroutineContext + SupervisorJob())

    init {
        subscribeLifecycle()
        _uiState.value = WaitingRoomState(isLoading = true, roomName = room.name)
        getUsers()
    }

    private fun subscribeLifecycle() {
        lifecycle.subscribe(onPause = {
            SocketManager.removeListener()
        }, onResume = {
            SocketManager.setListener(this)
            SocketManager.requestRoomInfo()
        }, onDestroy = {
            scope.cancel()
        })
    }

    override fun onBackPressed() {
        popScreen()
    }

    private fun getUsers() {
        scope.launch {
            val response = repository.getUsers(SocketManager.getRoomUserIds())
            if (response is Resource.Success) {
                val users = response.data.map {
                    val color = Color(("ff" + it.avatarColor.removePrefix("#").lowercase()).toLong(16))
                    WaitingUser(
                        it.id, it.username.uppercase(), AvatarInfo(
                            it.id, it.getInitials(), color
                        )
                    )
                }
                _uiState.update {
                    WaitingRoomState(
                        roomName = room.name,
                        users = users,
                        isLoading = false
                    )
                }
                checkUsers()
            } else {
                val error = response as Resource.Error
                _uiState.update {
                    WaitingRoomState(
                        roomName = room.name,
                        isLoading = false,
                        error = error.error
                    )
                }
            }
        }
    }

    private fun checkUsers() {
        if (room.isAdmin) {
            val currentUsersCount = SocketManager.getRoomUserIds().size
            val isSingleModeReady = room.mode == Single && currentUsersCount == 2
            val isMultiModeReady = room.mode == Many && currentUsersCount == 3
            if (isSingleModeReady || isMultiModeReady) {
                startGame()
            }
        }
    }

    private fun startGame() {
        SocketManager.signalForGame(KEY_PREPARE_GAME)
    }

    override fun onConnected() {

    }

    override fun onDisconnected() {

    }

    override fun onFailure(reason: String) {

    }

    override fun onEvent(event: SocketEvent) {
        when (event) {
            is UserJoined,
            is UserLeft,
            is RoomInfo -> getUsers()
            PrepareForGame -> {
                scope.launch(Dispatchers.Main) {
                    openGame()
                }
            }
            else -> {}
        }
    }
}