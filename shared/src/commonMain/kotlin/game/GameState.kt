package game

import home.Player

data class GameState(
    val players: List<Player> = emptyList(),
    val isCurrentUser: Boolean = false,
    val currentTurnUserId: String = "",
    val currentUsername: String = "",
    val isDrawing: Boolean = false,
    val isChoosing: Boolean = false,
    val word: String = "",
    val totalRounds: Int = 0,
    val currentRound: Int = 0,
    val totalTimeInSec: Int = 20,
    val currentTime: Int = 0,
) {
    val isCurrentUserChoosing = isCurrentUser && isChoosing
    val isOtherUserChoosing = !isCurrentUser && isChoosing
    val isCurrentUserDrawing = isCurrentUser && isDrawing
    val isOtherUserDrawing = !isCurrentUser && isDrawing
}

sealed class GameIntent {

}