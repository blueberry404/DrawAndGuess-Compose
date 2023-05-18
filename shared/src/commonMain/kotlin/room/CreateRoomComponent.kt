package room

import com.arkivanov.decompose.ComponentContext

interface CreateRoomComponent {
    fun onBackPressed()
    fun requestRoomCreation()
}

class DefaultCreateRoomComponent(
    componentContext: ComponentContext,
    private val popScreen: () -> Unit,
): CreateRoomComponent {
    override fun onBackPressed() {
        popScreen()
    }

    override fun requestRoomCreation() {

    }
}