package hu.bme.aut.tron.service

import hu.bme.aut.tron.api.BoardRecord

interface BoardDatabase {
    fun getBoard(): List<BoardRecord>

    suspend fun updateBoardWithWinner(winner: BoardRecord)
}
