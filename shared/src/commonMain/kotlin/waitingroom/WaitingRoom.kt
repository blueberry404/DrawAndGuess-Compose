package waitingroom

data class WaitingRoom(val id: String, val name: String, val users: List<WaitingUser>)

data class WaitingUser(val id: String, val name: String)