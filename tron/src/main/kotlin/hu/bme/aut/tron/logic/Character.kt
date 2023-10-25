package hu.bme.aut.tron.logic

import hu.bme.aut.tron.api.BikeInfo
import hu.bme.aut.tron.api.Direction

abstract class Character(
    val name: String,
    var colorId: Byte
) {
    var ready: Boolean = false
    abstract suspend fun move(x: Int, y: Int, timeout: Long): Direction
    abstract suspend fun currentState(newMap: List<List<Byte>>, routes: List<BikeInfo>)
    abstract suspend fun die()
}