package game.undo

import game.CanvasState

//Memento
data class CanvasSnapshot(val state: CanvasState)