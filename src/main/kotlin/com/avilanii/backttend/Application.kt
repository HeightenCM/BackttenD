package com.avilanii.backttend

import com.avilanii.backttend.plugins.configureMonitoring
import com.avilanii.backttend.di.configureFrameworks
import com.avilanii.backttend.infrastructure.database.configureDatabase
import com.avilanii.backttend.plugins.configureSecurity
import com.avilanii.backttend.plugins.configureSerialization
import com.avilanii.backttend.presentation.routes.configureRouting
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSecurity()
    configureMonitoring()
    configureSerialization()
    configureDatabase()
    configureFrameworks()
    configureRouting()
}
