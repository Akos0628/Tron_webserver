package hu.bme.aut.tron.api

import kotlinx.serialization.Serializable

@Serializable
data class BikeInfo(
    val colorId: Byte,
    val isAlive: Boolean,
    val route: List<Position>
)
