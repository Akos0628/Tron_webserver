package hu.bme.aut.tron.service

object MapGenerator {
    fun generateNew(height: Int, width: Int): List<List<Byte>> {
        val newMap = mutableListOf<List<Byte>>()
        for (y in (1..height)) {
            val column = mutableListOf<Byte>()
            for (x in (1..width)) {
                if (y == 1 || y == height) {
                    column.add(1)
                } else if (x == 1 || x == width) {
                    column.add(1)
                } else {
                    column.add(0)
                }
            }
            newMap.add(column)
        }
        return newMap
    }
}