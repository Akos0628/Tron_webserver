package hu.bme.aut.tron.service

import hu.bme.aut.tron.data.Lobby
import hu.bme.aut.tron.helpers.getRandomString

object LobbyService {
    private var lobbies = emptyMap<String, Lobby>()

    fun exists(id: String): Boolean = lobbies.containsKey(id)

    fun getLobby(id: String): Lobby? = lobbies[id]

    fun getAll() = lobbies.values

    fun createNewLobby(): String {
        var lobbyId = getRandomString(8)
        while (lobbies.containsKey(lobbyId)) {
            lobbyId = getRandomString(8)
        }
        lobbies += lobbyId to Lobby(lobbyId)
        return lobbyId
    }
}