package game

import home.Player

data class GameState(
    val players: List<Player> = emptyList(),
    val isCurrentUser: Boolean = false,
    val isDrawing: Boolean = false,
    val isChoosing: Boolean = false,
    val word: String = "",
    val totalRounds: Int = 0,
    val currentRound: Int = 0,
    val totalTimeInSec: Int = 20,
    val currentTime: Int = 0,
)

sealed class GameIntent {

}