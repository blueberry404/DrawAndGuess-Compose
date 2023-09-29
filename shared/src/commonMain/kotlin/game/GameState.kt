package game

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import core.serializers.ColorSerializer
import core.serializers.OffsetSerializer
import game.RoundState.Choosing
import game.RoundState.Drawing
import game.RoundState.Ended
import game.RoundState.Starting
import game.RoundState.TimeOver
import home.Player
import kotlinx.serialization.Serializable

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
    val polygons: List<CanvasPolygon> = mutableListOf(),
    val gameOverMessage: String = "",
) {
    val isDrawing: Boolean = roundState == Drawing
    val isStarting: Boolean = roundState == Starting
    val isTimeOver: Boolean = roundState == TimeOver
    val isGameOver: Boolean = roundState == Ended
    val isCurrentUserChoosing = isCurrentUser && roundState == Choosing
    val isOtherUserChoosing = !isCurrentUser && roundState == Choosing
    val isCurrentUserDrawing = isCurrentUser && roundState == Drawing
    val isOtherUserDrawing = !isCurrentUser && roundState == Drawing
}

data class GameWord(val actual: String = "", val guessed: String = "", val wiggle: Boolean = false)

data class DrawingInfo(val strokeWidth: Float = 4f, val paintColor: Color = Color.Red)

@Serializable
data class CanvasPolygon(
    val offsets: MutableList<@Serializable(OffsetSerializer::class) Offset> = mutableListOf(),
    val strokeWidth: Float = 4f,
    @Serializable(ColorSerializer::class) val paintColor: Color = Color.Red
)

@Serializable
data class CanvasState(val polygons: MutableList<CanvasPolygon>)