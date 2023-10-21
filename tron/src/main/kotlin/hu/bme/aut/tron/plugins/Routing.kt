package hu.bme.aut.tron.plugins

import hu.bme.aut.tron.api.BoardRecord
import hu.bme.aut.tron.api.Leaderboard
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.Instant

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
                            date = Instant.now().toEpochMilli(),
                            numPlayers = 2,
                            numBots = 1,
                            difficulty = "hard"
                        ),
                        BoardRecord(
                            name = "Boti",
                            score = 90,
                            date = Instant.now().minusSeconds(360000).toEpochMilli(),
                            numPlayers = 2,
                            numBots = 1,
                            difficulty = "hard"
                        ),
                        BoardRecord(
                            name = "Tomi",
                            score = 80,
                            date = Instant.now().minusSeconds(300000).toEpochMilli(),
                            numPlayers = 2,
                            numBots = 1,
                            difficulty = "hard"
                        ),
                        BoardRecord(
                            name = "Ákos",
                            score = 70,
                            date = Instant.now().minusSeconds(150000).toEpochMilli(),
                            numPlayers = 2,
                            numBots = 1,
                            difficulty = "hard"
                        )
                    )
                )
            )
        }
    }
}
