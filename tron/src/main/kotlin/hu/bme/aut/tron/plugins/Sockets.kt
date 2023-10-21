package hu.bme.aut.tron.plugins

import hu.bme.aut.tron.api.JoinMessage
import hu.bme.aut.tron.api.LeaveMessage
import hu.bme.aut.tron.api.Message
import hu.bme.aut.tron.api.StepMessage
import hu.bme.aut.tron.data.Player
import hu.bme.aut.tron.service.LobbyService
import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import java.time.Duration

private val formatter = Json {
    ignoreUnknownKeys = true
    serializersModule = SerializersModule {
        polymorphic(Message::class) {
            subclass(JoinMessage::class)
            subclass(StepMessage::class)
            subclass(LeaveMessage::class)
        }
    }
}

fun Application.configureSockets() {
    install(WebSockets) {
        contentConverter = KotlinxWebsocketSerializationConverter(formatter)
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(180)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    routing {
        webSocket("/lobby/{id?}") {
            val lobbyId = call.parameters["id"]!!
            send(lobbyId)
            if(LobbyService.exists(lobbyId)) {
                val lobby = LobbyService.getLobby(lobbyId)!!
                var session = this to ""

                try {
                    send("Checking lobby status")
                    val joinMessage = receiveDeserialized<JoinMessage>()

                    val player = Player(joinMessage.name, session.first)
                    session = session.first to player.id
                    var joined = true
                    while (joined) {
                        val message = receiveDeserialized<Message>()
                        // Itt határozzuk meg, hogy milyen üzenetet kell lekezelni
                        val messageType = when (message) {
                            is StepMessage -> send("chat")
                            is LeaveMessage -> {
                                send("leave1")
                                joined = false
                            }
                            else -> send("meh")
                        }
                    }
                } catch (e: Exception) {
                    println(e.localizedMessage)
                } finally {
                    println("Removing $session!")
                    lobby.disconnect(session.second)
                }
            }
        }
    }
}