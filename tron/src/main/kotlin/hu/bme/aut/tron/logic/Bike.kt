package hu.bme.aut.tron.logic

import hu.bme.aut.tron.api.BikeInfo
import hu.bme.aut.tron.api.Direction
import hu.bme.aut.tron.api.Position

class Bike(
    private var driver: Driver,
    var position: Position
) {
    val route: MutableList<Position> = mutableListOf()
    var isAlive = true
        private set

    var kills = 0

    init {
        route += position
    }
    fun moveTo(x: Int, y: Int) {
        position = Position(x,y)
        route += position
    }

    suspend fun requestStep(timeout: Long, botDelay: Long): Direction = driver.move(position.x, position.y, timeout, botDelay)

    suspend fun sendUpdate(map: List<List<Byte>>, routes: List<BikeInfo>) = driver.currentState(map, routes)

    suspend fun collide() {
        isAlive = false
        driver.die()
    }

    fun getColor(): Byte {
        return driver.colorId
    }

    fun getDriverName(): String {
        return driver.name
    }

    private fun aliveAsNum() = when(isAlive){
        true -> 1
        false -> 0
    }

    fun getScore(): Int {
        return POINTS_FOR_CELLS * route.size +
                POINTS_FOR_KILLS * kills +
                POINTS_FOR_LAST_ALIVE * aliveAsNum()
    }

    fun isReady(): Boolean {
        return driver.isReady()
    }

    fun shouldAppearOnLeaderBoard(): Boolean { return driver.shouldAppearOnLeaderBoard() }

    suspend fun countDown(sec: Int) {
        driver.sendCountDown(sec)
    }
}
