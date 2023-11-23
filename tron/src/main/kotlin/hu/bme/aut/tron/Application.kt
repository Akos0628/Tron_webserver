package hu.bme.aut.tron

import hu.bme.aut.tron.plugins.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

@Suppress("unused")
fun Application.module() {
    install(ContentNegotiation) {
        json()
    }

    configureSockets()
    configureRouting()
}
