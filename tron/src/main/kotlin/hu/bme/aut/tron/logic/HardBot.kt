package hu.bme.aut.tron.logic

import hu.bme.aut.tron.api.BikeInfo
import hu.bme.aut.tron.api.Direction
import hu.bme.aut.tron.api.Direction.*

class HardBot(
    private var map: List<List<Byte>>,
    name: String,
    colorId: Byte
) : Driver(name, colorId) {
    override suspend fun move(x: Int, y: Int, timeout: Long): Direction {
        val availableCells = mutableListOf<Direction>()
        if (map[y-1][x] == 0.toByte()) { availableCells.add(UP) }
        if (map[y][x-1] == 0.toByte()) { availableCells.add(LEFT) }
        if (map[y+1][x] == 0.toByte()) { availableCells.add(DOWN) }
        if (map[y+1][x+1] == 0.toByte()) { availableCells.add(RIGHT) }

        if (availableCells.isEmpty())
            availableCells.addAll(listOf(UP, RIGHT, DOWN, LEFT))

        return availableCells.random()
    }

    override suspend fun currentState(newMap: List<List<Byte>>, routes: List<BikeInfo>) {
        map = newMap
    }

    override suspend fun die() {
        // Do nothing
    }
}