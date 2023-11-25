package hu.bme.aut.tron.logic

import hu.bme.aut.tron.api.BikeInfo
import hu.bme.aut.tron.api.Direction
import hu.bme.aut.tron.api.Direction.*
import kotlinx.coroutines.delay

class EasyBot(
    private var map: List<List<Byte>>,
    name: String,
    colorId: Byte
) : Driver(name, colorId) {
    override suspend fun move(x: Int, y: Int, timeout: Long, botDelay: Long): Direction {
        delay(botDelay)
        return (listOf(UP, RIGHT, DOWN, LEFT).random())
    }

    override suspend fun currentState(newMap: List<List<Byte>>, routes: List<BikeInfo>) {
        map = newMap
    }

    override suspend fun die() {}

    override fun isReady(): Boolean { return true }

    override fun shouldAppearOnLeaderBoard(): Boolean { return false }

    override suspend fun sendCountDown(sec: Int) {}
}