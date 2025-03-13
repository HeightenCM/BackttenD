package com.avilanii.backttend.presentation.routes

import com.avilanii.backttend.domain.models.Event
import com.avilanii.backttend.services.EventService
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.eventsRoutes(
    eventService: EventService
) {
    route("/events") {
        get {
            val events = eventService.getAllEvents()
            if (events.isEmpty())
                call.respond(HttpStatusCode.NoContent, "No events found")
            call.respond(HttpStatusCode.OK, events)
        }
        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.NoContent, "Invalid id")
            }
            val event = eventService.getEventById(id!!)
            if (event == null) {
                call.respond(HttpStatusCode.NoContent, "Event with ID $id not found")
            } else{
                call.respond(HttpStatusCode.OK, event)
            }
        }
        post{
            val event = call.receive<Event>()
            val createdEventId = eventService.addEvent(event)
            if (createdEventId != null) {
                val createdEvent = eventService.getEventById(createdEventId)
                call.respond(HttpStatusCode.OK, createdEvent!!)
            } else
                call.respond(HttpStatusCode.NoContent, "Invalid event")
        }
    }
}