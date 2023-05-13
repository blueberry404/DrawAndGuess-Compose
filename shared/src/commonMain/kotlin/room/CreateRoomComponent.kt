package room

import com.arkivanov.decompose.ComponentContext

interface CreateRoomComponent {
    fun requestRoomCreation()
}

class DefaultCreateRoomComponent(
    componentContext: ComponentContext
): CreateRoomComponent {
    override fun requestRoomCreation() {

    }
}