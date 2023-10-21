package hu.bme.aut.tron.data

import io.ktor.websocket.*
import java.util.UUID.randomUUID

class Player(val name: String, val session: WebSocketSession) {
    val id: String = randomUUID().toString()
}
