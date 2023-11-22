package hu.bme.aut.tron.plugins

import hu.bme.aut.tron.api.Leaderboard
import hu.bme.aut.tron.helpers.EASY
import hu.bme.aut.tron.helpers.HARD
import hu.bme.aut.tron.service.Config
import hu.bme.aut.tron.service.FirebaseDb
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
                    FirebaseDb.getBoard().sortedByDescending {
                        it.score
                    }
                )
            )
        }
        get("/bots") {
            val response = client().get("${Config.requireProperty("ktor.bots.serverAddress")}get_bike_models") {
                accept(ContentType.Application.Json)
            }

            val list: List<String> = response.body()
            call.respond(list.plus(listOf(EASY, HARD)))
        }
        get("/test/nn/{matrix}") {
            val matrix = call.parameters["matrix"]!!
            println(matrix)

            val response = client().get("${Config.requireProperty("ktor.bots.serverAddress")}nnstep/$matrix")
            call.respond(response.body<String>())
        }
        get("/test/q/{matrix}") {
            val matrix = call.parameters["matrix"]!!
            println(matrix)

            val response = client().get("${Config.requireProperty("ktor.bots.serverAddress")}qstep/$matrix")
            call.respond(response.body<String>())
        }
        //get("/leaderboardChange") {
        //    call.respond(
        //        FirebaseDb.setBoard(
        //            listOf(
        //                BoardRecord(
        //                    name = "akos",
        //                    score = 0,
        //                    date = "2023-11-18 22:33",
        //                    numOfEnemies = 0
        //                ),
        //                BoardRecord(
        //                    name = "akos2",
        //                    score = 100,
        //                    date = "2023-11-18 22:33",
        //                    numOfEnemies = 1
        //                )
        //            )
        //        )
        //    )
        //}
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
