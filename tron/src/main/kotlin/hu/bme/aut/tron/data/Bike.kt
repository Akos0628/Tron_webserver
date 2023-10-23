package hu.bme.aut.tron.data;

import hu.bme.aut.tron.api.Direction
import io.ktor.server.websocket.*

class Bike(
    private var driver: Character,
    var position: Pair<Int, Int>
){
    val route: MutableList<Pair<Int, Int>> = mutableListOf()
    var isAlive = true
        private set

    init {
        route += position
    }
    fun moveTo(x: Int, y: Int) {
        position = Pair(x,y)
        route += position
    }

    suspend fun requestStep(game: Game) = driver.move(position.first, position.second, game, this)

    suspend fun sendUpdate(map: List<List<Byte>>, routes: List<Pair<Boolean,List<Pair<Int, Int>>>>) = driver.currentState(map, routes)

    suspend fun collide() {
        isAlive = false
        driver.die()
    }

    fun getColor(): Byte {
        return driver.colorId
    }

    fun isDriverOf(id: DefaultWebSocketServerSession): Boolean {
        println("aaaaa100")
        return driver.isSame(id)
    }
}
