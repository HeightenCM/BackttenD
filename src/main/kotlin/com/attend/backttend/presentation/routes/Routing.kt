package com.attend.com.attend.backttend.presentation.routes

import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        eventsRoutes()
    }
}
