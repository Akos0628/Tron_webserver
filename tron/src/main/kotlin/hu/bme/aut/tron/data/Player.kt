package hu.bme.aut.tron.data

import hu.bme.aut.tron.api.Direction
import hu.bme.aut.tron.api.MapUpdateMessage
import hu.bme.aut.tron.api.RequestStepMessage
import hu.bme.aut.tron.helpers.sendMessage
import io.ktor.server.websocket.*

class Player(
    name: String,
    colorId: Byte,
    private val session: DefaultWebSocketServerSession
) : Character(name, colorId) {
    override fun isSame(id: DefaultWebSocketServerSession): Boolean {
        println("aaaaa11")
        return session == id
    }

    override suspend fun move(x: Int, y: Int, game: Game, bike: Bike) {
        session.sendMessage(RequestStepMessage(x,y))
        session.sendSerialized(Direction.UP)
        session.sendSerialized(Direction.DOWN)
        session.sendSerialized(Direction.LEFT)
        session.sendSerialized(Direction.RIGHT)
        //TODO: Timeout and default step when player is slow to respond
    }

    override suspend fun currentState(newMap: List<List<Byte>>, routes: List<Pair<Boolean,List<Pair<Int, Int>>>>) {
        session.sendMessage(MapUpdateMessage(routes))
    }

    override suspend fun die() {
        TODO("Not yet implemented")
    }
}
