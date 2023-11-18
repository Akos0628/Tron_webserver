package hu.bme.aut.tron.service

import com.typesafe.config.ConfigFactory
import io.ktor.server.config.*

object Config {
    val config = HoconApplicationConfig(ConfigFactory.load())

    fun getProperty(key: String): String? = config.propertyOrNull(key)?.getString()

    fun requireProperty(key: String): String = getProperty(key)
            ?: throw IllegalStateException("Missing property $key")
}
