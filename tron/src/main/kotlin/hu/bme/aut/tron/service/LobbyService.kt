package hu.bme.aut.tron.service

import hu.bme.aut.tron.helpers.getRandomString
import hu.bme.aut.tron.logic.Lobby

object LobbyService {
    private var lobbies = emptyMap<String, Lobby>()

    fun exists(id: String): Boolean = lobbies.containsKey(id)

    fun getLobby(id: String): Lobby? = lobbies[id]

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