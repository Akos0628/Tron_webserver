package hu.bme.aut.tron.api

import kotlinx.serialization.Serializable

@Serializable
data class Leaderboard(
    val rows: List<BoardRecord>
)
