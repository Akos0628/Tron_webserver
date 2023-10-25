package hu.bme.aut.tron.logic

import hu.bme.aut.tron.api.BikeInfo
import hu.bme.aut.tron.api.Direction
import hu.bme.aut.tron.api.Position

class Bike(
    private var driver: Character,
    var position: Position
){
    val route: MutableList<Position> = mutableListOf()
    var isAlive = true
        private set

    init {
        route += position
    }
    fun moveTo(x: Int, y: Int) {
        position = Position(x,y)
        route += position
    }

    suspend fun requestStep(timeout: Long): Direction = driver.move(position.x, position.y, timeout)

    suspend fun sendUpdate(map: List<List<Byte>>, routes: List<BikeInfo>) = driver.currentState(map, routes)

    suspend fun collide() {
        isAlive = false
        driver.die()
    }

    fun getColor(): Byte {
        return driver.colorId
    }
}
