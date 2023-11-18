package hu.bme.aut.tron.api

import kotlinx.serialization.Serializable

@Serializable
data class Settings(
    val timeLimit: Long? = null,
    val turnTimeLimit: Long,
    val playerLimit: Int,
    val bots: List<RobotSettings>
)