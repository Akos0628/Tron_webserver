package hu.bme.aut.tron.data

abstract class Character(
    val name: String
) {
    var ready: Boolean = false
    abstract fun move()
    abstract fun die()
}