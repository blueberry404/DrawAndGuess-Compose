package waitingroom

data class WaitingRoomState(
    val isLoading: Boolean = false,
    val roomName: String = "",
    val users: List<WaitingUser> = emptyList()
)

data class WaitingUser(val id: String, val name: String)

