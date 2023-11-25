package hu.bme.aut.tron.service

import hu.bme.aut.tron.api.BoardRecord

object LeaderBoardService {
    private val db: BoardDatabase = FirebaseDb

    fun getBoard() = db.getBoard()

    suspend fun updateBoardWithWinner(winner: BoardRecord) = db.updateBoardWithWinner(winner)
}