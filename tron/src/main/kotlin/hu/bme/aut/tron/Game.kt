package hu.bme.aut.tron

import hu.bme.aut.tron.api.Direction

class Game(val id: String, var rows: Int, var cols: Int) {
    private var bikes: MutableList<Bike> = mutableListOf()
    private var map: MutableList<MutableList<Byte>> = mutableListOf()


    fun addPlayer(player: Bike) {
        bikes.add(player)
    }

    fun removePlayer(player: Bike) {
        bikes.remove(player)
    }

    //create map function
    //all cell is 0 at the beginning
    //the map size is given as a parameter
    //the edge of the map is 1
    fun generateMap(rows: Int, cols: Int) {
        for (i in 0..<rows) {
            val row: MutableList<Byte> = mutableListOf()
            for (j in 0..<cols) {
                if (i == 0 || i == rows-1 || j == 0 || j == cols-1) {
                    row.add(1)
                } else {
                    row.add(0)
                }
            }
            map.add(row)
        }
    }

    private fun generateWalls(){
        //TODO
    }

    fun playGame(){
        var isEnd = false
        while(!isEnd){
            for(player in bikes){
                val dir = player.getStep()
                val isStep = stepPlayer(player, dir)
                if(!isStep){
                    isEnd = true
                }
            }
        }

    }
    private fun stepPlayer(bike: Bike, dir: Direction): Boolean {
        var x = bike.x
        var y = bike.y
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
        return if (map[x][y] == 0.toByte()) {
            map[x][y] = bike.color
            bike.x = x
            bike.y = y
            true
        } else {
            false
        }
    }
}