package game

import androidx.compose.ui.geometry.Offset
import home.Player

data class GameState(
    val players: List<Player> = emptyList(),
    val isCurrentUser: Boolean = false,
    val currentTurnUserId: String = "",
    val currentUsername: String = "",
    val isDrawing: Boolean = false,
    val isChoosing: Boolean = false,
    val totalRounds: Int = 0,
    val currentRound: Int = 0,
    val totalTimeInSec: Int = 20,
    val currentTime: Int = 0,
    val word: GameWord = GameWord(),
) {
    val isCurrentUserChoosing = isCurrentUser && isChoosing
    val isOtherUserChoosing = !isCurrentUser && isChoosing
    val isCurrentUserDrawing = isCurrentUser && isDrawing
    val isOtherUserDrawing = !isCurrentUser && isDrawing
}

data class GameWord(val actual: String = "", val guessed: String = "", val wiggle: Boolean = false)

sealed class GameIntent {
    data class SelectLetter(val letter: Char): GameIntent()
    object OnDragStarted: GameIntent()
    data class OnDragMoved(val offset: Offset): GameIntent()
    object WiggleAnimationCompleted: GameIntent()
}