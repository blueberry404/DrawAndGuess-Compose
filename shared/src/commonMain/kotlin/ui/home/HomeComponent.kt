package ui.home

import androidx.compose.ui.graphics.Color
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.Lifecycle
import models.RoomContentMode
import models.RoomContentMode.Create
import models.RoomContentMode.Join
import models.GameMode.None
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import models.AvatarInfo
import models.GameMode
import network.DAGRepository
import network.Resource.Success
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.coroutines.CoroutineContext

interface HomeComponent {

    val state: StateFlow<HomeState>
    fun onGameOptionSelected(gameMode: GameMode)
    fun joinRoom()
}

class DefaultHomeComponent(
    componentContext: ComponentContext,
    coroutineContext: CoroutineContext,
    private val onOptionSelected: (GameMode, RoomContentMode) -> Unit,
) : HomeComponent, ComponentContext by componentContext, KoinComponent {

    private val repository: DAGRepository by inject()

    private var _state: MutableStateFlow<HomeState> = MutableStateFlow(HomeState(isLoading = true))
    private val scope = CoroutineScope(coroutineContext)

    init {
        fetchUserData()
        subscribeLifecycle()
    }

    private fun fetchUserData() {
        scope.launch {
            val response = repository.getUser()
            if (response is Success) {
                val user = response.data
                val color =
                    Color(("ff" + user.avatarColor.removePrefix("#").lowercase()).toLong(16))
                _state.update {
                    HomeState(
                        isLoading = false, user = HomeUserInfo(
                            welcomeText = "Welcome ${user.userName.uppercase()}!",
                            avatarInfo = AvatarInfo(user.id, user.getInitials(), color)
                        )
                    )
                }
            } else {
                val errorMessage = "Either you are not connected to internet or server is down"
                _state.update {
                    HomeState(isLoading = false, errorMessage = errorMessage)
                }
            }
        }
    }

    private fun subscribeLifecycle() {
        lifecycle.subscribe(object : Lifecycle.Callbacks {
            override fun onDestroy() {
                scope.cancel()
            }
        })
    }

    override val state: StateFlow<HomeState>
        get() = _state.asStateFlow()

    override fun onGameOptionSelected(gameMode: GameMode) {
        onOptionSelected(gameMode, Create)
    }

    override fun joinRoom() {
        onOptionSelected(None, Join)
    }
}