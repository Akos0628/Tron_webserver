package hu.bme.aut.tron.logic

import hu.bme.aut.tron.api.BikeInfo
import hu.bme.aut.tron.api.Direction

class Bot(
    private var map: List<List<Byte>>,
    name: String,
    colorId: Byte
) : Character(name, colorId) {
    init {
        ready = true
    }

    override suspend fun move(x: Int, y: Int, timeout: Long): Direction {
        return when ((0..3).random()) {
            0 -> Direction.UP
            1 -> Direction.RIGHT
            2 -> Direction.DOWN
            3 -> Direction.LEFT
            else -> Direction.UP
        }
    }

    override suspend fun currentState(newMap: List<List<Byte>>, routes: List<BikeInfo>) {
        map = newMap
    }

    override suspend fun die() {
        // Do nothing
    }
}