package hu.bme.aut.tron.api

import kotlinx.serialization.Serializable

@Serializable
enum class Direction {
    UP, DOWN, LEFT, RIGHT
}