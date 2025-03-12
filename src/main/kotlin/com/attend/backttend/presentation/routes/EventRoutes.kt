package com.attend.com.attend.backttend.presentation.routes

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.eventsRoutes() {
    route("/events") {
        get {
            call.respondText("List of all events", status = HttpStatusCode.OK)
        }
        get("/{id}") {
            val id = call.parameters["id"]!!
        }
    }
}