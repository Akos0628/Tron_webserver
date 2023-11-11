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
) : Driver(name, colorId) {
    public var ready = false
    private var routes: List<BikeInfo> = emptyList()
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
            var automaticResponse: Pair<Direction, Position>
            val myRoute = routes.find { it.colorId == colorId }!!.route

            do {
                automaticResponse = when ((0..3).random()) {
                    0 -> Direction.UP to Position(x,y+1)
                    1 -> Direction.RIGHT to Position(x+1,y)
                    2 -> Direction.DOWN to Position(x,y-1)
                    3 -> Direction.LEFT to Position(x-1,y)
                    else -> Direction.UP to Position(x,y+1)
                }
            } while (myRoute.last() == automaticResponse.second)
            session.sendMessage(TimeoutMessage("Response was too slow", automaticResponse.first))

            return@coroutineScope automaticResponse.first
        } else {
            return@coroutineScope response
        }
    }

    override suspend fun currentState(newMap: List<List<Byte>>, routes: List<BikeInfo>) {
        this.routes = routes
        session.sendMessage(MapUpdateMessage(routes))
    }

    override suspend fun die() {
        session.sendMessage(DieMessage("You died"))
    }
}
