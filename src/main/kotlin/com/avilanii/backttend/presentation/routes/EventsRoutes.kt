package com.avilanii.backttend.presentation.routes

import com.avilanii.backttend.domain.models.Event
import com.avilanii.backttend.infrastructure.datatransferobjects.toEventsResponseDTO
import com.avilanii.backttend.services.EventService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.eventsRoutes(
    eventService: EventService
) {
    authenticate("auth-jwt"){
        route("/events") {
            get {
                val userId = call.principal<JWTPrincipal>()?.payload?.subject

                val events = eventService.getAllEvents()
                call.respond(HttpStatusCode.OK, events.toEventsResponseDTO())
            }

            get("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respondText("Invalid id", status = HttpStatusCode.BadRequest)
                val event = eventService.getEventById(id) ?: return@get call.respondText("Event not found", status = HttpStatusCode.NotFound)
                call.respond(HttpStatusCode.OK, event)
            }

            post<Event>{ event->
                val createdEventId = eventService.addEvent(event) ?: return@post call.respondText("Couldn't create event", status = HttpStatusCode.BadRequest)
                val createdEvent = eventService.getEventById(createdEventId) ?: return@post call.respondText("Event not found", status = HttpStatusCode.NotFound)
                call.respond(HttpStatusCode.OK, createdEvent)
            }
        }
    }
}