package com.attend.com.attend.backttend.presentation.routes

import com.attend.com.attend.backttend.services.EventService
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

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