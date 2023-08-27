package createroom

data class CreateRoomState(
    val isLoading: Boolean = false,
    val roomName: String = "",
    val roomPassword: String = "",
    val buttonTitle: String = "",
    val showSnackBar: Boolean = false,
    val errorMessage: String = "",
)

sealed class CreateRoomIntent {
    data class OnRoomNameChanged(val roomName: String) : CreateRoomIntent()
    data class OnRoomPasswordChanged(val password: String) : CreateRoomIntent()
    object CreateRoom : CreateRoomIntent()
}

sealed class CreateRoomAction {

    object ShowWaitingLobby: CreateRoomAction()
}