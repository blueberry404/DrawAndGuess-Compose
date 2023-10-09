package ui.createroom

import core.widgets.DAGDialogInfo

data class CreateRoomState(
    val isLoading: Boolean = false,
    val roomName: String = "",
    val roomPassword: String = "",
    val buttonTitle: String = "",
    val dialogInfo: DAGDialogInfo = DAGDialogInfo(),
)

sealed class CreateRoomIntent {
    data class OnRoomNameChanged(val roomName: String) : CreateRoomIntent()
    data class OnRoomPasswordChanged(val password: String) : CreateRoomIntent()
    object CreateRoom : CreateRoomIntent()
    object DismissDialog : CreateRoomIntent()
}

sealed class CreateRoomAction {

    object ShowWaitingLobby: CreateRoomAction()
}