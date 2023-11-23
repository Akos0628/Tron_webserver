package hu.bme.aut.tron.logic

import hu.bme.aut.tron.api.*
import hu.bme.aut.tron.helpers.EASY
import hu.bme.aut.tron.helpers.HARD
import hu.bme.aut.tron.helpers.isInside
import hu.bme.aut.tron.service.LeaderBoardService
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

const val POINTS_FOR_LAST_ALIVE = 1000
const val POINTS_FOR_KILLS = 300
const val POINTS_FOR_CELLS = 5

class Game(
    players: List<Player>,
    private val settings: Settings,
    originalMap: List<List<Byte>>,
    availableColors: List<Byte>,
    botTypes: List<String>
) {
    private val bikes: List<Bike>
    private var map: List<MutableList<Byte>> = listOf()

    init {
        map = originalMap.map { it.toMutableList() }.toMutableList()
        var botNameId = 0
        val colors = availableColors.toMutableList()

        val bots = settings.bots.map {
            val color = colors.first()
            colors -= color

            when(it.type) {
                HARD -> HardBot(map,"hard bot${botNameId++}", color)
                EASY -> EasyBot(map,"easy bot${botNameId++}", color)
                else -> if (botTypes.contains(it.type)) {
                    BotClient(map,"${it.type} bot${botNameId++}", color, it.type)
                } else {
                    EasyBot(map,"easy bot${botNameId++}", color)
                }
            }

        }

        bikes = (players + bots).map {
            var x: Int
            var y: Int

            do {
                x = Random.nextInt(WIDTH)
                y = Random.nextInt(HEIGHT)
            } while (map[y][x] != 0.toByte())

            map[y][x] = it.colorId
            Bike(
                it,
                Position(x,y)
            )
        }
    }

    suspend fun playGame(): List<BoardRecord> {
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

        val current = SimpleDateFormat("yyyy-MM-dd HH:mm").format(Calendar.getInstance().time)

        val gameLeaderboard = bikes.map {
            BoardRecord(
                name = it.getDriverName(),
                score = it.getScore(),
                date = current,
                numOfEnemies = bikes.size-1
            )
        }.sortedByDescending {
            it.score
        }
        println(gameLeaderboard)
        LeaderBoardService.updateBoardWith(gameLeaderboard)

        return gameLeaderboard
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
        if (map.isInside(x,y)) {
            val nextCell = map[y][x]
            if (nextCell == 0.toByte()) {   // Üres cellára lép
                bike.moveTo(x,y)
                map[y][x] = bike.getColor()
            } else if (nextCell == 1.toByte()) {    // Falra lép
                bike.collide()
            } else if (map[y][x] == bike.getColor()) {  // Saját cellára
                bike.collide()
            } else {    // Más cellájára
                val killer = bikes.find { it.getColor() == map[y][x] }!!
                killer.kills++

                bike.collide()
            }
        } else {    // Pályán kívül
            bike.collide()
        }
    }
}