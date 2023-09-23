package game

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import game.RoundState.Choosing
import game.RoundState.Drawing
import game.RoundState.Starting
import home.Player

data class GameState(
    val players: List<Player> = emptyList(),
    val isCurrentUser: Boolean = false,
    val currentTurnUserId: String = "",
    val currentUsername: String = "",
    val totalRounds: Int = 0,
    val roundState: RoundState = Choosing,
    val totalTimeInSec: Int = 20,
    val currentTime: Int = 0,
    val word: GameWord = GameWord(),
    val drawingInfo: DrawingInfo = DrawingInfo(),
    val forceRestoreState: Boolean = false,
    val polygons: List<CanvasPolygon> = mutableListOf()
) {
    val isDrawing: Boolean = roundState == Drawing
    val isStarting: Boolean = roundState == Starting
    val isCurrentUserChoosing = isCurrentUser && roundState == Choosing
    val isOtherUserChoosing = !isCurrentUser && roundState == Choosing
    val isCurrentUserDrawing = isCurrentUser && roundState == Drawing
    val isOtherUserDrawing = !isCurrentUser && roundState == Drawing
}

data class GameWord(val actual: String = "", val guessed: String = "", val wiggle: Boolean = false)

data class DrawingInfo(val strokeWidth: Float = 4f, val paintColor: Color = Color.Red)

data class CanvasPolygon(
    val offsets: MutableList<Offset> = mutableListOf(),
    val strokeWidth: Float = 4f,
    val paintColor: Color = Color.Red
)

data class CanvasState(val polygons: List<CanvasPolygon>)