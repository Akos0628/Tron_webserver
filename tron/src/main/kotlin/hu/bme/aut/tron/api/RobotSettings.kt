package hu.bme.aut.tron.api

import kotlinx.serialization.Serializable

@Serializable
data class RobotSettings(
    val type: String,
    val difficulty: Difficulty
)
