package hu.bme.aut.tron.helpers

import hu.bme.aut.tron.api.*
import io.ktor.server.websocket.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

const val EASY = "easy"
const val HARD = "hard"

val formatter = Json {
    ignoreUnknownKeys = true
    serializersModule = SerializersModule {
        polymorphic(ClientMessage::class) {
            subclass(JoinMessage::class)
            subclass(LeaveMessage::class)
            subclass(SettingsMessage::class)
            subclass(NewMapMessage::class)
            subclass(NextColorMessage::class)
            subclass(ReadyMessage::class)
            subclass(StepMessage::class)
        }
        polymorphic(ServerMessage::class) {
            subclass(BadMessage::class)
            subclass(LeavingMessage::class)
            subclass(MapMessage::class)
            subclass(SettingsChangedMessage::class)
            subclass(LobbyFullMessage::class)
            subclass(PlayersMessage::class)
            subclass(CountDownMessage::class)
            subclass(StartMessage::class)
            subclass(RequestStepMessage::class)
            subclass(MapUpdateMessage::class)
            subclass(TimeoutMessage::class)
            subclass(DieMessage::class)
            subclass(GameOverMessage::class)
        }
    }
}

fun getRandomString(length: Int) : String {
    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    return (1..length)
        .map { allowedChars.random() }
        .joinToString("")
}

suspend inline fun DefaultWebSocketServerSession.sendMessage(msg: ServerMessage) {
    sendSerialized(msg)
}

fun List<List<Byte>>.isInside(x: Int, y: Int): Boolean = x >= 0 && y >= 0 && x < this.size && y < this[0].size