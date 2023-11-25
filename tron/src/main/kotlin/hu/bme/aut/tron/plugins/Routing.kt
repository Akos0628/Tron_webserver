package hu.bme.aut.tron.plugins

import hu.bme.aut.tron.api.Leaderboard
import hu.bme.aut.tron.helpers.EASY
import hu.bme.aut.tron.helpers.HARD
import hu.bme.aut.tron.service.Config
import hu.bme.aut.tron.service.LeaderBoardService
import hu.bme.aut.tron.service.LobbyService
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hail, stranger, who seeks to play our wondrous Tron game.\n" +
                    "\n" +
                    "Welcome to our humble kingdom, where we play a game of skill and speed.\n" +
                    "\n" +
                    "The goal of the game is to be the first player to reach the finish line.\n" +
                    "\n" +
                    "You will need to use your reflexes and agility to avoid your opponents' light cycles.\n" +
                    "\n" +
                    "If you are brave enough to challenge us, we will be glad to play.")
        }
        get("/leaderboard") {
            call.respond(
                Leaderboard(
                    LeaderBoardService.getBoard()
                )
            )
        }
        get("/bots") {
            val response = client.get("${Config.requireProperty("ktor.bots.serverAddress")}get_bike_models") {
                accept(ContentType.Application.Json)
            }

            val list: List<String> = response.body()
            call.respond(list.plus(listOf(EASY, HARD)))
        }
        route("/lobbies") {
            get {
                call.respond(LobbyService.getAllOpen().map { it.id })
            }
            get("/{id}") {
                val id = call.parameters["id"]!!
                call.respond(LobbyService.getLobby(id)!!.visibility)
            }
            get("/new") {
                call.respondText { LobbyService.createNewLobby() }
            }
        }
    }
}
