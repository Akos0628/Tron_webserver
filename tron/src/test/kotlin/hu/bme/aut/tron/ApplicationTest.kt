package hu.bme.aut.tron

import hu.bme.aut.tron.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        application {
            configureRouting()
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Hail, stranger, who seeks to play our wondrous Tron game.\n" +
                    "\n" +
                    "Welcome to our humble kingdom, where we play a game of skill and speed.\n" +
                    "\n" +
                    "The goal of the game is to be the first player to reach the finish line.\n" +
                    "\n" +
                    "You will need to use your reflexes and agility to avoid your opponents' light cycles.\n" +
                    "\n" +
                    "If you are brave enough to challenge us, we will be glad to play.", bodyAsText())
        }
    }
}
