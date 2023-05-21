package home

import createroom.RoomContentMode
import createroom.RoomContentMode.Create
import createroom.RoomContentMode.Join
import home.GameMode.None

interface HomeComponent {
    fun onGameOptionSelected(gameMode: GameMode)
    fun joinRoom()
}

class DefaultHomeComponent(
    private val onOptionSelected: (GameMode, RoomContentMode) -> Unit,
): HomeComponent {

    override fun onGameOptionSelected(gameMode: GameMode) {
        onOptionSelected(gameMode, Create)
    }

    override fun joinRoom() {
        onOptionSelected(None, Join)
    }
}