package game

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

sealed class GameIntent {
    data class SelectLetter(val letter: Char) : GameIntent()
    object OnDragStarted : GameIntent()
    data class OnDragMoved(val offset: Offset) : GameIntent()
    object OnDragEnded : GameIntent()
    object WiggleAnimationCompleted : GameIntent()
    data class SelectColor(val color: Color) : GameIntent()
    data class SelectStrokeWidth(val width: Float) : GameIntent()
    object Undo : GameIntent()
    object ClearCanvas : GameIntent()
    object Erase : GameIntent()
    object StateRestoreCompleted : GameIntent()
}