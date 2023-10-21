package hu.bme.aut.tron.api

import kotlinx.serialization.Serializable

@Serializable
data class Robot(
    val type: String
)
