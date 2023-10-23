package hu.bme.aut.tron.data

import io.ktor.server.websocket.*

abstract class Character(
    val name: String,
    var colorId: Byte
) {
    var ready: Boolean = false
    abstract fun isSame(id: DefaultWebSocketServerSession): Boolean
    abstract suspend fun move(x: Int, y: Int, game: Game, bike: Bike)
    abstract suspend fun currentState(newMap: List<List<Byte>>, routes: List<Pair<Boolean,List<Pair<Int, Int>>>>)
    abstract suspend fun die()
}