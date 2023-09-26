package game.undo

import game.CanvasPolygon
import game.CanvasState

//Caretaker
class CanvasCommand {
    private val snapshots = ArrayDeque<CanvasState>()

    fun save(polygon: CanvasPolygon) {
        snapshots.lastOrNull()?.polygons?.let {
            val newList = it.toMutableList()
            newList.add(polygon)
            snapshots.addLast(CanvasState(newList))
        } ?:
        snapshots.addLast(CanvasState(mutableListOf(polygon)))
    }

    fun clear() {
        snapshots.addLast(CanvasState(mutableListOf()))
    }

    fun undo(): CanvasState? {
        snapshots.removeLastOrNull()
        return snapshots.lastOrNull()
    }

    fun peek(): CanvasState? {
        return snapshots.lastOrNull()
    }
}