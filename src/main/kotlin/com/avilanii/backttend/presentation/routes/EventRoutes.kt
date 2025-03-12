package com.avilanii.backttend.presentation.routes

import com.avilanii.backttend.services.EventService
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.eventsRoutes() {
    route("/events") {
        get {
            val events = EventService().getAllEvents()
            call.respond(events[1])
        }
        get("/{id}") {
            val id = call.parameters["id"]!!
        }
    }
}