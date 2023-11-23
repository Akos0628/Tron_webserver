package hu.bme.aut.tron.plugins

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*

val client = HttpClient {
        install(ContentNegotiation) {
            json()
        }
    }