package waitingroom

import androidx.compose.ui.graphics.Color
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import core.GlobalData
import home.AvatarInfo
import home.GameMode.Many
import home.GameMode.Single
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import network.DAGRepository
import network.Resource
import sockets.SocketEvent
import sockets.SocketEvent.UserJoined
import sockets.SocketEvent.UserLeft
import sockets.SocketEventsListener
import sockets.SocketManager
import kotlin.coroutines.CoroutineContext

class WaitingRoomViewModel(
    coroutineContext: CoroutineContext,
) : InstanceKeeper.Instance, SocketEventsListener {

    private var _uiState = MutableStateFlow(WaitingRoomState())
    val uiState: StateFlow<WaitingRoomState> = _uiState.asStateFlow()

    private val room = GlobalData.room
    private val scope = CoroutineScope(coroutineContext + SupervisorJob())
    private val repository = DAGRepository()

    init {
        _uiState.value = WaitingRoomState(isLoading = true, roomName = room.name)
        getUsers()
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
        SocketManager.signalGameStart()
    }

    override fun onConnected() {

    }

    override fun onDisconnected() {

    }

    override fun onFailure(reason: String) {

    }

    override fun onEvent(event: SocketEvent) {
        when (event) {
            is UserJoined -> getUsers()
            is UserLeft -> getUsers()
        }
    }

    override fun onDestroy() {
        SocketManager.removeListener()
        scope.cancel()
    }
}