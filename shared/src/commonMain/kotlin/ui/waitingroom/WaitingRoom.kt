package ui.waitingroom

import models.AvatarInfo

data class WaitingRoomState(
    val isLoading: Boolean = false,
    val roomName: String = "",
    val users: List<WaitingUser> = emptyList(),
    val error: String = "",
)

data class WaitingUser(val id: String, val name: String, val info: AvatarInfo)

