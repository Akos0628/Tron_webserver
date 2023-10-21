package hu.bme.aut.tron.api

import kotlinx.serialization.Serializable

@Serializable
data class BoardRecord(
    val name: String,
    val score: Int,
    val date: Long,
    val numPlayers: Int,
    val numBots: Int,
    val difficulty: String
)
