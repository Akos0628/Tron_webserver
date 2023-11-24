package hu.bme.aut.tron.service

import kotlin.math.abs
import kotlin.math.ceil
import kotlin.random.Random

object MapGenerator {
    private fun drawWall(map: Array<IntArray>): Array<IntArray> {
        val minWallLen = 5
        if (map.size > map[0].size && map.size > map[0].size + minWallLen) {
            val half = (map[0].size / 2)
            for (i in 3..<map.size - 3) {
                map[i][half] = 1
            }
        } else {
            if (map.size < map[0].size && map.size + minWallLen < map[0].size) {
                val half = (map.size / 2)
                for (i in 3..<map[0].size - 3) {
                    map[half][i] = 1
                }
            }
        }
        return map
    }

    private fun recursion(map: Array<IntArray>, depth: Int): Array<IntArray> {
        if (map.isEmpty()) {
            return emptyArray()
        }
        var newMap = map
        val minP = 2
        val maxP = 5
        val mapH = newMap.size
        val mapW = newMap[0].size
        val widthBigger = mapH < mapW
        val p = (Random.nextInt(minP, maxP) * 2 + 1).toDouble()
        if (widthBigger) {
            if (mapH < p) {
                if (mapH < minP * 2 + 1) {
                    newMap.forEach { it.fill(0) }
                } else {
                    drawWall(newMap)
                }
                return newMap
            } else {
                newMap = if (mapW > p * 4 + 1) {
                    val cut = Random.nextInt(minP * 4 + 1, mapW - (minP * 2 + 1))
                    val mapX1 = recursion(newMap.map { it.copyOfRange(0, cut) }.toTypedArray(), depth + 1)
                    val mapX2 = recursion(newMap.map { it.copyOfRange(cut, mapW) }.toTypedArray(), depth + 1)
                    mapX1.mapIndexed { index, ints ->
                        (ints + mapX2[index])
                    }.toTypedArray()
                } else {
                    val mapX1 = recursion(newMap.map { it.copyOfRange(0, p.toInt()) }.toTypedArray(), depth + 1)
                    val mapX2 = recursion(newMap.map { it.copyOfRange(p.toInt(), mapW) }.toTypedArray(), depth + 1)
                    mapX1.mapIndexed { index, ints ->
                        (ints + mapX2[index])
                    }.toTypedArray()
                }
                return newMap
            }
        } else {
            if (mapW < p) {
                if (mapW < minP * 2 + 1) {
                    newMap.forEach { it.fill(0) }
                } else {
                    drawWall(newMap)
                }
                return newMap
            } else {
                newMap = if (mapH > p * 4 + 1) {
                    val cut = Random.nextInt(minP * 4 + 1, mapH - (minP * 2 + 1))
                    recursion(newMap.copyOfRange(0, cut), depth + 1) +
                            recursion(newMap.copyOfRange(cut, mapH), depth + 1)
                } else {
                    recursion(newMap.copyOfRange(0, p.toInt()), depth + 1) +
                            recursion(newMap.copyOfRange(p.toInt(), mapH), depth + 1)
                }
                return newMap
            }
        }
    }

    private fun findWalls(mapData: Array<IntArray>): List<List<Pair<Int, Int>>> {
        val walls = mutableListOf<MutableList<Pair<Int, Int>>>()
        mapData.forEachIndexed { y, row ->
            row.forEachIndexed { x, cell ->
                if (cell == 1) {
                    var wallFound = false
                    for ((adjacentRow, adjacentCol) in listOf(
                        Pair(y - 1, x),
                        Pair(y, x + 1),
                        Pair(y + 1, x),
                        Pair(y, x - 1)
                    )) {
                        if (adjacentRow in mapData.indices && adjacentCol in mapData[0].indices) {
                            walls.forEach { mapTile ->
                                if (Pair(adjacentRow, adjacentCol) in mapTile) {
                                    mapTile.add(Pair(y, x))
                                    wallFound = true
                                }

                            }
                        }
                    }
                    if (!wallFound) {
                        walls.add(mutableListOf(Pair(y, x)))
                    }
                }
            }
        }
        return walls
    }

    private fun drawWallsBetweenClosest(mapData: Array<IntArray>, walls: List<List<Pair<Int, Int>>>): Array<IntArray> {
        val closestPairs = mutableListOf<Triple<Int, List<Pair<Pair<Int, Int>, Pair<Int, Int>>>, Pair<Int, Int>>>()
        for (i in walls.indices) {
            for (j in i + 1..<walls.size) {
                val (distance, coordinates) = calculateDistance(walls[i], walls[j])
                closestPairs.add(Triple(distance, coordinates, i to j))
            }
        }
        closestPairs.sortBy { it.first }
        var newMapData: Array<IntArray> = mapData
        closestPairs.take(walls.size)
            .forEach { (_, coordinates, _) ->
                newMapData = drawWallBetween(newMapData, coordinates).copyOf()
            }
        return newMapData
    }

    private fun calculateDistance(wall1: List<Pair<Int, Int>>, wall2: List<Pair<Int, Int>>): Pair<Int, List<Pair<Pair<Int, Int>, Pair<Int, Int>>>> {
        var minDistance = Int.MAX_VALUE
        val minCoordinates = mutableListOf<Pair<Pair<Int, Int>, Pair<Int, Int>>>()
        for (coordinates1 in wall1) {
            for (coordinates2 in wall2) {
                val distance =
                    abs(coordinates1.first - coordinates2.first) + abs(coordinates1.second - coordinates2.second)
                if (distance < minDistance) {
                    minDistance = distance
                    minCoordinates.clear()
                }
                if (distance == minDistance) {
                    minCoordinates.add(Pair(coordinates1, coordinates2))
                }
            }
        }
        return Pair(minDistance, minCoordinates)
    }

    private fun drawWallBetween(mapData: Array<IntArray>, coordinates: List<Pair<Pair<Int, Int>, Pair<Int, Int>>>): Array<IntArray> {
        val connect = Random.nextInt(0, coordinates.size)
        val (coordinates1, coordinates2) = coordinates[connect]
        val newWallTiles = mutableListOf<Pair<Int, Int>>()
        if (abs(coordinates1.first - coordinates2.first) != 1) {
            val newRow = coordinates1.first.coerceAtMost(coordinates2.first)
            for (col in coordinates1.second.coerceAtMost(coordinates2.second) + 1..<coordinates1.second.coerceAtLeast(
                coordinates2.second
            )) {
                newWallTiles.add(Pair(newRow, col))
            }
        } else if (abs(coordinates1.second - coordinates2.second) != 1) {
            val newCol = coordinates1.second.coerceAtMost(coordinates2.second)
            for (row in coordinates1.first.coerceAtMost(coordinates2.first) + 1..<coordinates1.first.coerceAtLeast(
                coordinates2.first
            )) {
                newWallTiles.add(Pair(row, newCol))
            }
        }
        for ((row, col) in newWallTiles) {
            if (mapData[row][col] == 1) {
                return mapData
            }
        }
        for ((row, col) in newWallTiles) {
            mapData[row][col] = 1
        }
        return mapData
    }

    fun generateNew(height: Int, width: Int): List<List<Byte>> {
        val ranInt = Random.nextInt(0, 3)
        val h: Int
        val w: Int
        val symmetric: Int
        val minWallNum: Int
        when (ranInt) {
            0 -> {
                h = height
                w = width
                symmetric = 0
                minWallNum = 5
            }
            1 -> {
                h = height
                w = ceil(width / 2.0).toInt()
                symmetric = 1
                minWallNum = 4
            }
            else -> {
                h = ceil(height / 2.0).toInt()
                w = ceil(width / 2.0).toInt()
                symmetric = 2
                minWallNum = 3
            }
        }
        var map = Array(h) { IntArray(w) }
        val wallPercentage = 5
        var found = false
        while (!found) {
            map = Array(h) { IntArray(w) }
            map = recursion(map, 0)
            val wallTilesNum = map.sumOf { it.sum() }
            found = wallTilesNum > h * w * (wallPercentage * 0.01)
            if (found) {
                val walls = findWalls(map)
                found = walls.size >= minWallNum
            }
        }
        drawWallsBetweenClosest(map, findWalls(map))
        if (symmetric > 0) {
            map = map.map { it + it.reversedArray() }.toTypedArray()
            if (symmetric == 2) {
                map += map.reversedArray()
            }
        }

        return map.map { row ->
            row.map { it.toByte() }
        }.toList()
    }
}