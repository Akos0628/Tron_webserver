package hu.bme.aut.tron.data

import hu.bme.aut.tron.api.Visibility
import io.ktor.websocket.*

class Lobby(val id: String) {
    val visibility: Visibility = Visibility.CLOSED
    val status: LobbyStatus = LobbyStatus.WAITING
    var players: Map<String, Player> = emptyMap()

    fun playerJoin(player: Player) {
        if(players.count() < 4)
        players += (player.id to player)
    }

    fun disconnect(playerId: String) {
        players -= playerId
    }

    suspend fun sendMsgToPlayers(msg: String) {
        players.forEach { (_, player) ->
            player.session.send(msg)
        }
    }
}