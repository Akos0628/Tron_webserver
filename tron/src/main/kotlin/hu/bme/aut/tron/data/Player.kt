package hu.bme.aut.tron.data

import io.ktor.server.websocket.*

class Player(
    name: String,
    var colorId: Int,
    private val session: DefaultWebSocketServerSession
) : Character(name) {
    override fun move() {
        TODO("Not yet implemented")
    }

    override fun die() {
        TODO("Not yet implemented")
    }
}
