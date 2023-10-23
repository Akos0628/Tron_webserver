package hu.bme.aut.tron.data

import hu.bme.aut.tron.api.Direction
import io.ktor.server.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Bot(
    private var map: List<List<Byte>>,
    name: String,
    colorId: Byte
) : Character(name, colorId) {
    init {
        ready = true
    }

    override fun isSame(id: DefaultWebSocketServerSession): Boolean {
        return false
    }

    override suspend fun move(x: Int, y: Int, game: Game, bike: Bike): Unit = coroutineScope {
        launch {
            while (game.currentStepper != null) {
                delay(500L)
            }

            val dir = when((0..3).random()) {
                0 -> Direction.UP
                1 -> Direction.RIGHT
                2 -> Direction.DOWN
                3 -> Direction.LEFT
                else -> Direction.UP
            }
            game.stepPlayer(bike, dir)
        }
    }

    override suspend fun currentState(newMap: List<List<Byte>>, routes: List<Pair<Boolean,List<Pair<Int, Int>>>>) {
        map = newMap
    }

    override suspend fun die() {
        TODO("Not yet implemented")
    }
}