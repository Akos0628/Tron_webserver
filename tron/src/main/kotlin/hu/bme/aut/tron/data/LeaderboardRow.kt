package hu.bme.aut.tron.data

import java.util.Date

data class LeaderboardRow(
    val name: String,
    val score: Int,
    val date: Date,
    val numPlayers: Int,
    val numBots: Int,
    val difficulty: String)
