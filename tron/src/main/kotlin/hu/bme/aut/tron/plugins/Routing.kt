package hu.bme.aut.tron.plugins

import hu.bme.aut.tron.api.Leaderboard
import hu.bme.aut.tron.service.LeaderBoardService
import hu.bme.aut.tron.service.LobbyService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/leaderboard") {
            call.respond(
                Leaderboard(
                    LeaderBoardService.getBoard()
                )
            )
        }
        route("/lobbies") {
            get("/new") {
                call.respondText { LobbyService.createNewLobby() }
            }
        }
    }
}
