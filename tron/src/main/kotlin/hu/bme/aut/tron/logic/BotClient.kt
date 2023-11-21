package hu.bme.aut.tron.logic

import hu.bme.aut.tron.api.BikeInfo
import hu.bme.aut.tron.api.Direction
import hu.bme.aut.tron.plugins.client
import hu.bme.aut.tron.service.Config
import io.ktor.client.call.*
import io.ktor.client.request.*
import java.lang.IllegalArgumentException

class BotClient(
    private var map: List<List<Byte>>,
    name: String,
    colorId: Byte,
    private val type: String
) : Driver(name, colorId) {
    private val baseUrl = Config.requireProperty("ktor.bots.serverAddress")

    override suspend fun move(x: Int, y: Int, timeout: Long): Direction {
        val list = transformMap(x,y)
        val response = client().get("$baseUrl$type/${list.joinToString(",")}").body<Int>()

        println("Client AI choose: $response")

        return when(response) {
            0 -> Direction.UP
            1 -> Direction.LEFT
            2 -> Direction.DOWN
            3 -> Direction.RIGHT
            else -> { throw IllegalArgumentException() }
        }
    }

    override suspend fun currentState(newMap: List<List<Byte>>, routes: List<BikeInfo>) {
        map = newMap
    }

    override suspend fun die() {
        // Do nothing
    }

    fun transformMap(x: Int, y: Int): List<Int> {
        return listOf(
            map[x-1][y-1],
            map[x][y-1],
            map[x+1][y-1],
            map[x-1][y],
            map[x][y],
            map[x+1][y],
            map[x-1][y+1],
            map[x][y+1],
            map[x+1][y+1],
        ).map { it.asCell() }
    }

    private fun Byte.asCell() = when(this.toInt()) {
        0 -> 0
        else -> 1
    }
}