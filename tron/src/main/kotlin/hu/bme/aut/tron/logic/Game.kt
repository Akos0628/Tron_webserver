package hu.bme.aut.tron.logic

import hu.bme.aut.tron.api.BikeInfo
import hu.bme.aut.tron.api.Direction
import hu.bme.aut.tron.api.Position
import hu.bme.aut.tron.api.Settings
import kotlin.random.Random

class Game(
    players: List<Player>,
    private val settings: Settings,
    originalMap: List<List<Byte>>,
    availableColors: List<Byte>
) {
    private val bikes: List<Bike>
    private var map: MutableList<MutableList<Byte>> = mutableListOf()

    init {
        map = originalMap.map { it.toMutableList() }.toMutableList()
        var botNameId = 0
        val colors = availableColors.toMutableList()

        val bots = settings.bots.map {
            val color = colors.random()
            colors -= color

            Bot(
                map,
                "bot${botNameId++}",
                color
            )
        }

        bikes = (players + bots).map {
            var x: Int
            var y: Int

            do {
                x = Random.nextInt(settings.mapSize.x)
                y = Random.nextInt(settings.mapSize.y)
            } while (map[x-1][y-1] != 0.toByte())

            Bike(
                it,
                Position(x,y)
            )
        }
    }

    suspend fun playGame() {
        handleRoutes()

        while(bikes.all { it.isAlive }){
            bikes.filter { it.isAlive }.forEach { bike ->
                val dir = bike.requestStep(settings.turnTimeLimit)
                stepPlayer(bike, dir)
                handleRoutes()
            }
        }
    }

    private suspend fun handleRoutes() {
        val bikeRoutes = bikes.map { BikeInfo(it.isAlive, it.route) }
        bikes.forEach { it.sendUpdate(map, bikeRoutes) }
    }

    private suspend fun stepPlayer(bike: Bike, dir: Direction) {
        //TODO: Index 63 out of bounds for length 63
        var x = bike.position.x
        var y = bike.position.y
        when (dir) {
            Direction.UP -> {
                x--
            }
            Direction.DOWN -> {
                x++
            }
            Direction.LEFT -> {
                y--
            }
            Direction.RIGHT -> {
                y++
            }
        }
        if (map[x-1][y-1] == 0.toByte()) {
            map[x-1][y-1] = bike.getColor()
            bike.moveTo(x,y)
        } else {
            bike.collide()
        }
    }
}