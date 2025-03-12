package com.avilanii.backttend.presentation.routes

import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        eventsRoutes()
    }
}
