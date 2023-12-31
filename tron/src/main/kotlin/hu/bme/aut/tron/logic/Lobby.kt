package hu.bme.aut.tron.logic

import hu.bme.aut.tron.api.*
import hu.bme.aut.tron.helpers.EASY
import hu.bme.aut.tron.helpers.HARD
import hu.bme.aut.tron.helpers.sendMessage
import hu.bme.aut.tron.plugins.client
import hu.bme.aut.tron.service.Config
import hu.bme.aut.tron.service.LobbyService
import hu.bme.aut.tron.service.MapGenerator
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.server.websocket.*
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration

const val COUNT_DOWN_SEC = 3
const val MAX_PLAYER_LIMIT = 8
const val HEIGHT = 26
const val WIDTH = 46
const val TURN_LIMIT = "5s"

class Lobby(private val id: String) {
    var status: LobbyStatus = LobbyStatus.WAITING
    private var players: Map<DefaultWebSocketServerSession, Player> = emptyMap()
    private var gameSettings = Settings(
        playerLimit = 2,
        turnTimeLimit = Duration.parse(TURN_LIMIT).inWholeMilliseconds,
        bots = emptyList()
    )
    private var availableColors = (2..10).map { it.toByte() }.toMutableList()

    private lateinit var gameMap: List<List<Byte>>
    private lateinit var leader: DefaultWebSocketServerSession
    private var availableBots: List<String>

    init {
        runBlocking {
            val botTypesResponse = async { client.get("${Config.requireProperty("ktor.bots.serverAddress")}get_bike_models") }
            generateNewMap()
            availableBots = try {
                botTypesResponse.await().body<List<String>>().plus(listOf(EASY, HARD))
            } catch (e: Exception) {
                println("Bot server is unavailable")
                listOf(EASY, HARD)
            }
        }
    }

    suspend fun generateMapSafe(initiator: DefaultWebSocketServerSession) {
        if (initiator != leader) {
            initiator.sendMessage(BadMessage("You are not authorized"))
        } else {
            generateNewMap()
        }
    }

    private suspend fun generateNewMap() {
        gameMap = MapGenerator.generateNew(HEIGHT, WIDTH)

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

            id.sendMessage(YourColorMessage(player.colorId))
            id.sendMessage(SettingsChangedMessage(gameSettings, availableBots))
            id.sendMessage(MapMessage(gameMap))
            sendLobbyPlayers()
        } else if(players.count() + gameSettings.bots.size < gameSettings.playerLimit) {
            players += (id to player)

            id.sendMessage(YourColorMessage(player.colorId))
            id.sendMessage(SettingsChangedMessage(gameSettings, availableBots))
            id.sendMessage(MapMessage(gameMap))
            sendLobbyPlayers()
        } else {
            id.sendMessage(LobbyFullMessage(player.name))
            return false
        }
        return true
    }

    suspend fun sendRefresh(session: DefaultWebSocketServerSession) {
        val playerInfos = players.map { (id, player) ->
            ApiPlayer(
                player.name,
                player.colorId,
                leader == id,
                player.ready
            )
        }

        session.sendMessage(PlayersMessage(playerInfos))
        session.sendMessage(SettingsChangedMessage(gameSettings, availableBots))
        session.sendMessage(MapMessage(gameMap))
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

        session.sendMessage(YourColorMessage(player.colorId))
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
        } else {
            gameSettings = settings
            players.forEach { (session, _) ->
                session.sendMessage(SettingsChangedMessage(gameSettings, availableBots))
            }
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
                player.inGame = false
            }

            val game = Game(
                players.values.toList(),
                gameSettings,
                gameMap,
                availableColors,
                availableBots
            )

            val gameResult = game.playGame()

            players.forEach { (session, player) ->
                player.endedGame()
                val data = gameResult.firstOrNull { it.first == player.colorId }
                data?.let { session.sendMessage(GameOverMessage(it.second, it.third)) }
            }
            status = LobbyStatus.WAITING
        }
    }
}