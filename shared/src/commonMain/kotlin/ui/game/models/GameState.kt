package ui.game.models

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import core.serializers.ColorSerializer
import core.serializers.OffsetSerializer
import ui.game.models.TurnState.Choosing
import ui.game.models.TurnState.Drawing
import ui.game.models.TurnState.Ended
import ui.game.models.TurnState.Starting
import ui.game.models.TurnState.TimeOver
import models.Player
import kotlinx.serialization.Serializable

data class GameState(
    val players: List<Player> = emptyList(),
    val isCurrentUser: Boolean = false,
    val currentTurnUserId: String = "",
    val currentUsername: String = "",
    val totalRounds: Int = 0,
    val turnState: TurnState = Choosing,
    val totalTimeInSec: Int = 20,
    val currentTime: Int = 0,
    val word: GameWord = GameWord(),
    val drawingInfo: DrawingInfo = DrawingInfo(),
    val forceRestoreState: Boolean = false,
    val polygons: List<CanvasPolygon> = mutableListOf(),
    val gameOverMessage: String = "",
) {
    val isDrawing: Boolean = turnState == Drawing
    val isStarting: Boolean = turnState == Starting
    val isTimeOver: Boolean = turnState == TimeOver
    val isGameOver: Boolean = turnState == Ended
    val isCurrentUserChoosing = isCurrentUser && turnState == Choosing
    val isOtherUserChoosing = !isCurrentUser && turnState == Choosing
    val isCurrentUserDrawing = isCurrentUser && turnState == Drawing
    val isOtherUserDrawing = !isCurrentUser && turnState == Drawing
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