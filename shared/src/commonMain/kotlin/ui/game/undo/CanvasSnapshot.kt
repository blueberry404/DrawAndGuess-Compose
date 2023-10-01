package ui.game.undo

import ui.game.models.CanvasState

//Memento
data class CanvasSnapshot(val state: CanvasState)