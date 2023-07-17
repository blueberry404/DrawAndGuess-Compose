package home

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import network.DAGRepository
import network.Resource.Success
import kotlin.coroutines.CoroutineContext

class HomeViewModel(
    coroutineContext: CoroutineContext,
) : InstanceKeeper.Instance {

    private val scope = CoroutineScope(coroutineContext)
    private val repository = DAGRepository()

    init {
        fetchUserData()
    }

    private fun fetchUserData() {
        scope.launch {
            val response = repository.getUser()
            if (response is Success) {

            }
            else {

            }
        }
    }

    override fun onDestroy() {
        scope.cancel()
    }
}