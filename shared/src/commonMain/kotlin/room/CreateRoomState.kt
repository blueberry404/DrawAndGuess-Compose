package room

data class CreateRoomState(
    val isLoading: Boolean = false,
    val roomName: String = "",
    val roomPassword: String = ""
)

sealed class CreateRoomIntent {
    data class OnRoomNameChanged(val roomName: String) : CreateRoomIntent()
    data class OnRoomPasswordChanged(val password: String) : CreateRoomIntent()
    object CreateRoom : CreateRoomIntent()
}

sealed class CreateRoomAction {
    data class ShowDialog(val icon: String, val message: String, val buttonTitle: String) :
        CreateRoomAction()
}