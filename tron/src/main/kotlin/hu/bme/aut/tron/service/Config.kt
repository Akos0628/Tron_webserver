package hu.bme.aut.tron.service

import com.typesafe.config.ConfigFactory
import io.ktor.server.config.*

object Config {
    private val config = HoconApplicationConfig(ConfigFactory.load())

    private fun getProperty(key: String): String? = config.propertyOrNull(key)?.getString()

    fun requireProperty(key: String): String = getProperty(key)
            ?: throw IllegalStateException("Missing property $key")
}
