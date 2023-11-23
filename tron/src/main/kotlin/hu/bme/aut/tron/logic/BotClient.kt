package hu.bme.aut.tron.logic

import hu.bme.aut.tron.api.BikeInfo
import hu.bme.aut.tron.api.Direction
import hu.bme.aut.tron.helpers.getCellWalled
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

        val direction = when(response) {
            0 -> Direction.UP
            1 -> Direction.LEFT
            2 -> Direction.DOWN
            3 -> Direction.RIGHT
            else -> { throw IllegalArgumentException() }
        }

        println("$name choose: $direction")

        return direction
    }

    override suspend fun currentState(newMap: List<List<Byte>>, routes: List<BikeInfo>) {
        map = newMap
    }

    override suspend fun die() {
        // Do nothing
    }

    private fun transformMap(x: Int, y: Int): List<Int> {
        return listOf(
            map.getCellWalled(y+1, x-1),
            map.getCellWalled(y+1, x),
            map.getCellWalled(y+1, x+1),
            map.getCellWalled(y, x-1),
            map.getCellWalled(y, x),
            map.getCellWalled(y, x+1),
            map.getCellWalled(y-1, x-1),
            map.getCellWalled(y-1, x),
            map.getCellWalled(y-1, x+1),
        ).map { it.asCell() }
    }

    private fun Byte.asCell() = when(this.toInt()) {
        0 -> 0
        else -> 1
    }
}