package waitingroom

interface WaitingRoomComponent {
    fun onBackPressed()
    fun onGamersArrived()
}

class DefaultWaitingRoomComponent(
    private val popScreen: () -> Unit,
): WaitingRoomComponent {

    override fun onBackPressed() {
        popScreen()
    }

    override fun onGamersArrived() {
        TODO("Not yet implemented")
    }
}