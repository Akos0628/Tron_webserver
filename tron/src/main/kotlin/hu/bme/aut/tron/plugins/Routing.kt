package hu.bme.aut.tron.plugins

import hu.bme.aut.tron.api.BoardRecord
import hu.bme.aut.tron.api.Leaderboard
import hu.bme.aut.tron.service.LobbyService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.text.SimpleDateFormat
import java.util.*

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
                    listOf(
                        BoardRecord(
                            name = "Anna",
                            score = 100,
                            date = SimpleDateFormat("yyyy-MM-dd HH:mm").format(Calendar.getInstance().time),
                            numOfEnemies = 3
                        ),
                        BoardRecord(
                            name = "Boti",
                            score = 90,
                            date = SimpleDateFormat("yyyy-MM-dd HH:mm").format(Calendar.getInstance().time),
                            numOfEnemies = 3
                        ),
                        BoardRecord(
                            name = "Tomi",
                            score = 80,
                            date = SimpleDateFormat("yyyy-MM-dd HH:mm").format(Calendar.getInstance().time),
                            numOfEnemies = 2
                        ),
                        BoardRecord(
                            name = "Ákos",
                            score = 70,
                            date = SimpleDateFormat("yyyy-MM-dd HH:mm").format(Calendar.getInstance().time),
                            numOfEnemies = 1
                        )
                    )
                )
            )
        }
        route("/lobbies") {
            get {
                call.respond(LobbyService.getAllOpen().map { it.id })
            }
            get("/{id}") {
                val id = call.parameters["id"]!!
                call.respond(LobbyService.getLobby(id)!!.visibility)
            }
            post("/create") {
                call.respondText { LobbyService.createNewLobby() }
            }
        }
    }
}
