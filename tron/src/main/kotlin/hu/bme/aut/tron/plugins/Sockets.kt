package hu.bme.aut.tron.plugins

import hu.bme.aut.tron.api.*
import hu.bme.aut.tron.logic.LobbyStatus
import hu.bme.aut.tron.logic.Player
import hu.bme.aut.tron.helpers.formatter
import hu.bme.aut.tron.helpers.sendMessage
import hu.bme.aut.tron.service.LobbyService
import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import java.time.Duration

const val WRONG_MESSAGE = "Can't use that function in the current state of the game"

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
            println("Trying to join to lobby: $lobbyId")
            if(LobbyService.exists(lobbyId)) {
                val lobby = LobbyService.getLobby(lobbyId)!!
                val session = this

                try {
                    println("Checking lobby status")
                    val joinMessage = receiveDeserialized<JoinMessage>()

                    val player = Player(joinMessage.name, lobby.getAvailableColorId(), session)
                    var joined = lobby.playerJoin(session, player)
                    while (joined) {
                        val clientMessage = receiveDeserialized<ClientMessage>()
                        when (lobby.status) {
                            LobbyStatus.WAITING -> {
                                when (clientMessage) {
                                    is NewMapMessage -> {
                                        println("Received NewMapMessage")
                                        lobby.generateMapSafe(session)
                                    }
                                    is NextColorMessage -> {
                                        println("Received NextColorMessage")
                                        lobby.rollNextColor(session)
                                    }
                                    is SettingsMessage -> {
                                        println("Received SettingsMessage")
                                        lobby.newSettings(session, clientMessage.settings)
                                    }
                                    is ReadyMessage -> {
                                        println("Received ReadyMessage")
                                        lobby.handleReady(session, clientMessage.value)
                                    }
                                    is LeaveMessage -> {
                                        println("Received LeaveMessage")
                                        joined = false
                                    }
                                    is JoinMessage -> {
                                        lobby.sendRefresh(session)
                                    }
                                    else -> {
                                        println("Received bad message")
                                        session.sendMessage(BadMessage(WRONG_MESSAGE))
                                    }
                                }
                            }
                            LobbyStatus.GAME -> {
                                when (clientMessage) {
                                    is StepMessage -> {
                                        println("Received StepMessage")
                                        player.push(clientMessage)
                                    }
                                    is LeaveMessage -> {
                                        println("Received LeaveMessage")
                                        joined = false
                                    }
                                    else -> {
                                        println("Received bad message")
                                        session.sendMessage(BadMessage(WRONG_MESSAGE))
                                    }
                                }
                            }
                        }
                    }

                    session.sendMessage(LeavingMessage("Leaving the lobby"))
                } catch (e: Exception) {
                    println(e.localizedMessage)
                } finally {
                    try {
                        session.sendMessage(LeavingMessage("Disconnecting from the lobby"))
                    } catch (ex: Exception) {
                        println("session already closed")
                    }
                    println("Removing $session!")
                    lobby.disconnect(session)
                }
            } else {
                this.sendMessage(BadMessage("Not existing lobby"))
            }
        }
    }
}