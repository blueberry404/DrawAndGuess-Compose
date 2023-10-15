package ui.waitingroom

import core.widgets.DAGDialogInfo
import models.AvatarInfo

data class WaitingRoomState(
    val isLoading: Boolean = false,
    val roomName: String = "",
    val users: List<WaitingUser> = emptyList(),
    val error: String = "",
    val dialogInfo: DAGDialogInfo = DAGDialogInfo(),
)

data class WaitingUser(val id: String, val name: String, val info: AvatarInfo)

