package home

import androidx.compose.ui.graphics.Color
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import network.DAGRepository
import network.Resource.Success
import kotlin.coroutines.CoroutineContext

class HomeViewModel(
    coroutineContext: CoroutineContext,
) : InstanceKeeper.Instance {

    private val scope = CoroutineScope(coroutineContext)
    private val repository = DAGRepository()

    private var _state: MutableStateFlow<HomeState> = MutableStateFlow(HomeState(isLoading = true))
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        fetchUserData()
    }

    private fun fetchUserData() {
        scope.launch {
            val response = repository.getUser()
            if (response is Success) {
                val user = response.data
                val color = Color(("ff" + user.avatarColor.removePrefix("#").lowercase()).toLong(16))
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

    override fun onDestroy() {
        scope.cancel()
    }
}