package hu.bme.aut.tron.logic

import hu.bme.aut.tron.api.BikeInfo
import hu.bme.aut.tron.api.Direction
import hu.bme.aut.tron.api.Direction.*

class EasyBot(
    private var map: List<List<Byte>>,
    name: String,
    colorId: Byte
) : Driver(name, colorId) {
    override suspend fun move(x: Int, y: Int, timeout: Long): Direction {
        return (listOf(UP, RIGHT, DOWN, LEFT).random())
    }

    override suspend fun currentState(newMap: List<List<Byte>>, routes: List<BikeInfo>) {
        map = newMap
    }

    override suspend fun die() {
        // Do nothing
    }
}