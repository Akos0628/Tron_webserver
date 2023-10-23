package hu.bme.aut.tron.api

import kotlinx.serialization.Serializable

@Serializable
data class ApiPlayer(
    val name: String,
    val colorId: Byte,
    val isLeader: Boolean,
    val isReady: Boolean
)
