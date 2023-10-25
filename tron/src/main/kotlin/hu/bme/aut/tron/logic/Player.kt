package hu.bme.aut.tron.logic

import hu.bme.aut.tron.api.*
import hu.bme.aut.tron.helpers.sendMessage
import io.ktor.server.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class Player(
    name: String,
    colorId: Byte,
    private val session: DefaultWebSocketServerSession
) : Character(name, colorId) {
    private var stepMessageQueue = MutableSharedFlow<StepMessage>()
    suspend fun push(message: StepMessage) {
        stepMessageQueue.emit(message)
    }

    override suspend fun move(x: Int, y: Int, timeout: Long): Direction = coroutineScope {
        session.sendMessage(RequestStepMessage(x,y))

        val response = withTimeoutOrNull(timeout) {
            val step = stepMessageQueue.first()
            return@withTimeoutOrNull step.direction
        }

        if (response == null) {
            val automaticResponse = when ((0..3).random()) {
                0 -> Direction.UP
                1 -> Direction.RIGHT
                2 -> Direction.DOWN
                3 -> Direction.LEFT
                else -> Direction.UP
            }
            session.sendMessage(TimeoutMessage("Response was too slow", automaticResponse))

            return@coroutineScope automaticResponse
        } else {
            return@coroutineScope response
        }
    }

    override suspend fun currentState(newMap: List<List<Byte>>, routes: List<BikeInfo>) {
        session.sendMessage(MapUpdateMessage(routes))
    }

    override suspend fun die() {
        session.sendMessage(DieMessage("You died"))
    }
}
