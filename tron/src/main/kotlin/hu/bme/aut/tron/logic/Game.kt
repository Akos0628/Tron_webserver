package hu.bme.aut.tron.logic

import hu.bme.aut.tron.api.BikeInfo
import hu.bme.aut.tron.api.Direction
import hu.bme.aut.tron.api.Position
import hu.bme.aut.tron.api.Settings
import hu.bme.aut.tron.helpers.isInside
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
            } while (map[x][y] != 0.toByte())

            Bike(
                it,
                Position(x,y)
            )
        }
    }

    suspend fun playGame(): Byte {
        handleRoutes()

        var playing = true
        while(playing){
            bikes.filter { it.isAlive }.forEach { bike ->
                if (bikes.filter { it.isAlive }.size > 1) {
                    val dir = bike.requestStep(settings.turnTimeLimit)
                    stepPlayer(bike, dir)
                    handleRoutes()
                } else {
                    playing = false
                }
            }
        }

        return bikes.find { it.isAlive }!!.getColor()
    }

    private suspend fun handleRoutes() {
        val bikeRoutes = bikes.map { BikeInfo(it.getColor(), it.isAlive, it.route) }
        bikes.forEach { it.sendUpdate(map, bikeRoutes) }
    }

    private suspend fun stepPlayer(bike: Bike, dir: Direction) {
        var x = bike.position.x
        var y = bike.position.y
        when (dir) {
            Direction.UP -> {
                y++
            }
            Direction.DOWN -> {
                y--
            }
            Direction.LEFT -> {
                x--
            }
            Direction.RIGHT -> {
                x++
            }
        }
        if (map.isInside(x,y) && map[x][y] == 0.toByte()) {
            map[x][y] = bike.getColor()
            bike.moveTo(x,y)
        } else {
            bike.collide()
        }
    }
}