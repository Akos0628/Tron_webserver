package hu.bme.aut.tron.logic

import hu.bme.aut.tron.api.*
import hu.bme.aut.tron.helpers.sendMessage
import hu.bme.aut.tron.service.LobbyService
import io.ktor.server.websocket.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration

const val COUNT_DOWN_SEC = 3
const val MAX_PLAYER_LIMIT = 4
const val MIN_HEIGHT = 20
const val MAX_HEIGHT = 64
const val MIN_WIDTH = 36
const val MAX_WIDTH = 114
const val TURN_LIMIT = "1s"

class Lobby(val id: String) {
    val visibility: Visibility = Visibility.CLOSED
    var status: LobbyStatus = LobbyStatus.WAITING
    private var players: Map<DefaultWebSocketServerSession, Player> = emptyMap()
    private var gameSettings = Settings(
        playerLimit = 2,
        turnTimeLimit = Duration.parse(TURN_LIMIT).inWholeMilliseconds,
        bots = emptyList(),
        mapSize = Position(MAX_WIDTH, MAX_HEIGHT)
    )
    private var availableColors = (2..10).map { it.toByte() }.toMutableList()

    private lateinit var gameMap: List<List<Byte>>
    private lateinit var leader: DefaultWebSocketServerSession

    init {
        runBlocking {
            generateNewMap()
        }
    }

    suspend fun generateNewMap() {
        // Ide kell a pálya generálást megvalósítani
        gameMap = generateSequence {
            generateSequence {
                (0).toByte()
            }.take(gameSettings.mapSize.y).toList()
        }.take(gameSettings.mapSize.x).toList()

        // Innentől ne változtass
        players.forEach { (session, _) ->
            session.sendMessage(MapMessage(gameMap))
        }
    }

    suspend fun playerJoin(id: DefaultWebSocketServerSession, player: Player): Boolean {
        if(status != LobbyStatus.WAITING) {
            return false
        } else if(players.isEmpty()) {
            leader = id
            players += (id to player)
            id.sendMessage(SettingsChangedMessage(gameSettings))
            id.sendMessage(MapMessage(gameMap))
            sendLobbyPlayers()
        } else if(players.count() + gameSettings.bots.size < gameSettings.playerLimit) {
            players += (id to player)
            id.sendMessage(SettingsChangedMessage(gameSettings))
            id.sendMessage(MapMessage(gameMap))
            sendLobbyPlayers()
        } else {
            id.sendMessage(LobbyFullMessage(player.name))
            return false
        }
        return true
    }

    suspend fun disconnect(playerId: DefaultWebSocketServerSession) {
        val player = players[playerId]
        if (player != null) {
            availableColors += player.colorId
            players -= playerId
            if (players.isEmpty()) {
                LobbyService.removeLobby(id)
            } else {
                if (playerId == leader) {
                    leader = players.keys.random()
                }
                sendLobbyPlayers()
            }
        }
    }

    private suspend fun sendLobbyPlayers() {
        val playerInfos = players.map { (id, player) ->
            ApiPlayer(
                player.name,
                player.colorId,
                leader == id,
                player.ready
            )
        }
        players.forEach { (session, _) ->
            session.sendMessage(PlayersMessage(playerInfos))
        }
    }

    fun getAvailableColorId(): Byte {
        val selected = availableColors.min()
        availableColors -= selected
        return selected
    }

    suspend fun rollNextColor(session: DefaultWebSocketServerSession) {
        val player = players[session]!!
        val allColor = (availableColors + player.colorId).sorted()
        val selected = if (allColor.max() == player.colorId) {
            allColor.min()
        } else {
            allColor[allColor.indexOf(player.colorId)+1]
        }

        availableColors += player.colorId
        availableColors -= selected
        player.colorId = selected

        sendLobbyPlayers()
    }

    suspend fun newSettings(initiator: DefaultWebSocketServerSession, settings: Settings) {
        if (initiator != leader) {
            initiator.sendMessage(BadMessage("You are not authorized"))
        } else if (settings.playerLimit > MAX_PLAYER_LIMIT) {
            initiator.sendMessage(BadMessage("The maximum player limit is $MAX_PLAYER_LIMIT"))
        } else if (settings.playerLimit - (settings.bots.size + players.size) < 0) {
            initiator.sendMessage(BadMessage("The number of joined players (${players.size}) and the number of bots (${settings.bots.size}) exceeds the player limit (${settings.playerLimit})"))
        } else if (settings.turnTimeLimit < 1) {
            initiator.sendMessage(BadMessage("The time limit is too low"))
        } else if (settings.mapSize.x < MIN_HEIGHT || settings.mapSize.y < MIN_WIDTH) {
            initiator.sendMessage(BadMessage("Map size is too small"))
        } else if (settings.mapSize.x > MAX_HEIGHT || settings.mapSize.y > MAX_WIDTH) {
            initiator.sendMessage(BadMessage("Map size is too big"))
        } else {
            val previousMapSize = gameSettings.mapSize
            gameSettings = settings
            players.forEach { (session, _) ->
                session.sendMessage(SettingsChangedMessage(gameSettings))
            }

            if (previousMapSize != gameSettings.mapSize)
                generateNewMap()
        }
    }

    suspend fun handleReady(id: DefaultWebSocketServerSession, ready: Boolean) = coroutineScope {
        players[id]!!.ready = ready

        sendLobbyPlayers()

        if (players.all { it.value.ready }) {
            status = LobbyStatus.GAME
            delay(1000L)

            (COUNT_DOWN_SEC downTo 1).forEach { sec ->
                players.forEach { (session, _) ->
                    session.sendMessage(CountDownMessage(sec))
                }
                delay(1000L)
            }

            players.forEach { (session, player) ->
                session.sendMessage(StartMessage())
                player.ready = false
            }
            launch {
                val game = Game(
                    players.values.toList(),
                    gameSettings,
                    gameMap,
                    availableColors
                )
                val winnerId = game.playGame()
                status = LobbyStatus.FINISHED

                val winner = players.values.find { it.colorId == winnerId }!!
                players.forEach { (session, player) ->
                    if (player.colorId == winnerId) {
                        session.sendMessage(GameOverMessage(winnerId, "You won!"))
                    } else {

                        session.sendMessage(GameOverMessage(winnerId, "${winner.name} won!"))
                    }
                }
                //TODO: Store the data to the leaderboard
                delay(10000L)

                status = LobbyStatus.WAITING
            }
        }
    }
}