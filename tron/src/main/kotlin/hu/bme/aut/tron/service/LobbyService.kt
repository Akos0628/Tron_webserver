package hu.bme.aut.tron.service

import hu.bme.aut.tron.api.Visibility
import hu.bme.aut.tron.logic.Lobby
import hu.bme.aut.tron.helpers.getRandomString

object LobbyService {
    private var lobbies = emptyMap<String, Lobby>()

    fun exists(id: String): Boolean = lobbies.containsKey(id)

    fun getLobby(id: String): Lobby? = lobbies[id]

    fun getAllOpen() = lobbies.values.filter { it.visibility == Visibility.OPEN }.toList()

    fun removeLobby(id: String) {
        lobbies -= id
    }

    fun createNewLobby(): String {
        var lobbyId = getRandomString(6)
        while (lobbies.containsKey(lobbyId)) {
            lobbyId = getRandomString(6)
        }
        lobbies += lobbyId to Lobby(lobbyId)
        return lobbyId
    }
}