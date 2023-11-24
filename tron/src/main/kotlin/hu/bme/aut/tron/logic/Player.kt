package hu.bme.aut.tron.logic

import hu.bme.aut.tron.api.*
import hu.bme.aut.tron.helpers.getCellSafe
import hu.bme.aut.tron.helpers.sendMessage
import io.ktor.server.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class Player(
    name: String,
    colorId: Byte,
    private val session: DefaultWebSocketServerSession
) : Driver(name, colorId) {
    private lateinit var map: List<List<Byte>>
    var ready = false
    var inGame = false
    private var routes: List<BikeInfo> = emptyList()
    private var stepMessageQueue = MutableSharedFlow<StepMessage>()
    suspend fun push(message: StepMessage) {
        stepMessageQueue.emit(message)
    }

    override suspend fun move(x: Int, y: Int, timeout: Long, botDelay: Long): Direction = coroutineScope {
        session.sendMessage(RequestStepMessage(x,y))

        val response = if (session.isActive) {
            withTimeoutOrNull(timeout) {
                val step = stepMessageQueue.first()
                return@withTimeoutOrNull step.direction
            }
        } else { null }

        if (response == null) {
            val availableCells = mutableListOf<Direction>()
            if (map.getCellSafe(y+1, x) == 0.toByte()) { availableCells.add(Direction.UP) }
            if (map.getCellSafe(y, x-1) == 0.toByte()) { availableCells.add(Direction.LEFT) }
            if (map.getCellSafe(y-1, x) == 0.toByte()) { availableCells.add(Direction.DOWN) }
            if (map.getCellSafe(y, x+1) == 0.toByte()) { availableCells.add(Direction.RIGHT) }

            if (availableCells.isEmpty())
                availableCells.addAll(listOf(Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT))

            val automaticResponse = availableCells.random()
            session.sendMessage(TimeoutMessage("Response was too slow", automaticResponse))

            return@coroutineScope automaticResponse
        } else {
            return@coroutineScope response
        }
    }

    override suspend fun currentState(newMap: List<List<Byte>>, routes: List<BikeInfo>) {
        this.routes = routes
        map = newMap
        session.sendMessage(MapUpdateMessage(routes))
    }

    override suspend fun die() {
        session.sendMessage(DieMessage("You died"))
    }

    override fun isReady(): Boolean {
        return inGame
    }

    override suspend fun sendCountDown(sec: Int) {
        session.sendMessage(CountDownMessage(sec))
    }

    fun endedGame() {
        inGame = false
    }
}
