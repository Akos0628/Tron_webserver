package hu.bme.aut.tron.service

import hu.bme.aut.tron.api.BoardRecord

object LeaderBoardService {
    private val db: BoardDatabase = FirebaseDb
    suspend fun updateBoardWith(leaderboard: List<BoardRecord>) {
        val original = db.getBoard()
        val new = (original + leaderboard).sortedByDescending {
            it.score
        }

        db.setBoard(new)
    }
}