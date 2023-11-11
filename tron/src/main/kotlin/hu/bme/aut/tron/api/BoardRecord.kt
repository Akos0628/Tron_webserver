package hu.bme.aut.tron.api

import kotlinx.serialization.Serializable

@Serializable
data class BoardRecord(
    val name: String,
    val score: Int,
    val date: String,
    val numOfEnemies: Int
)
