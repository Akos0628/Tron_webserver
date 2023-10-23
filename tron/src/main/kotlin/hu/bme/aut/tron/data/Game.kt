package hu.bme.aut.tron.data

import hu.bme.aut.tron.api.Direction
import hu.bme.aut.tron.api.Settings
import io.ktor.server.websocket.*
import kotlinx.coroutines.delay
import kotlin.random.Random

class Game(
    players: List<Player>,
    settings: Settings,
    originalMap: List<List<Byte>>,
    availableColors: List<Byte>
) {
    private val bikes: List<Bike>
    private var map: MutableList<MutableList<Byte>> = mutableListOf()
    var currentStepper: Bike? = null

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
            Bike(
                it,
                Pair(Random.nextInt(settings.mapSize.second),Random.nextInt(settings.mapSize.first))
            )
        }
    }

    suspend fun playGame() {
        handleRoutes()

        while(bikes.all { it.isAlive }){
            bikes.filter { it.isAlive }.forEach { bike ->
                bike.requestStep(this)
                println("aaaaa5")
                currentStepper = bike
                while (currentStepper != null) {
                    delay(100L)
                }
                println("aaaaa6")
                handleRoutes()
            }
        }
    }

    private suspend fun handleRoutes() {
        val bikeRoutes = bikes.map { Pair(it.isAlive, it.route) }
        bikes.forEach { it.sendUpdate(map, bikeRoutes) }
    }

    suspend fun handlePlayerMove(id: DefaultWebSocketServerSession, dir: Direction) {
        println("aaaaa3")
        if (currentStepper?.isDriverOf(id) == true) {
            println("aaaa7a")
            stepPlayer(currentStepper!!, dir)
        }
    }

    suspend fun stepPlayer(bike: Bike, dir: Direction) {
        //TODO: Index 63 out of bounds for length 63
        println("aaaaa8")
        var x = bike.position.first
        var y = bike.position.second
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
        if (map[x][y] == 0.toByte()) {
            map[x][y] = bike.getColor()
            bike.moveTo(x,y)
        } else {
            bike.collide()
        }

        currentStepper = null
    }
}