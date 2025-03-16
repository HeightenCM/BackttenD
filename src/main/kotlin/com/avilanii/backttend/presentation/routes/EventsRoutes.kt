package com.avilanii.backttend.presentation.routes

import com.avilanii.backttend.domain.models.Event
import com.avilanii.backttend.domain.models.Participant
import com.avilanii.backttend.domain.models.ParticipantRole
import com.avilanii.backttend.domain.models.ParticipantStatus
import com.avilanii.backttend.infrastructure.datatransferobjects.toEventsResponseDTO
import com.avilanii.backttend.services.EventService
import com.avilanii.backttend.services.ParticipantService
import com.avilanii.backttend.services.UserService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.time.LocalDateTime

fun Route.eventsRoutes(
    eventService: EventService
) {
    authenticate("auth-jwt"){
        route("/events") {
            get {
                val userId = call.principal<JWTPrincipal>()?.payload?.subject!!.toInt()
                val role = call.principal<JWTPrincipal>()?.payload?.getClaim("role")?.`as`(ParticipantRole::class.java)?:ParticipantRole.ORGANIZER
                val events = eventService.getAllEvents(userId, role)
                call.respond(HttpStatusCode.OK, events.toEventsResponseDTO())
            }

            get("/{id}") {
                val userId = call.principal<JWTPrincipal>()?.payload?.subject!!.toInt()
                val eventId = call.parameters["id"]?.toIntOrNull() ?: return@get call.respondText("Invalid id", status = HttpStatusCode.BadRequest)
                val event = eventService.getEventById(userId, eventId) ?: return@get call.respondText("Event not found", status = HttpStatusCode.NotFound)
                call.respond(HttpStatusCode.OK, event)
            }

            post<Event>{ event->
                val userId = call.principal<JWTPrincipal>()?.payload?.subject!!.toInt()
                val createdEventId = eventService.addEvent(event)
                val createdEvent = eventService.getEventById(userId, createdEventId) ?: return@post call.respondText("Event not created", status = HttpStatusCode.BadRequest)
                val participantService by inject<ParticipantService>()
                val userService by inject<UserService>()
                val user = userService.findById(userId)!!
                participantService.addParticipant(Participant(
                    eventId = createdEventId,
                    name = user.name,
                    email = user.email,
                    status = ParticipantStatus.ACCEPTED,
                    role = ParticipantRole.ORGANIZER,
                    joinDate = LocalDateTime.now().toString()
                ))
                call.respond(HttpStatusCode.OK, createdEvent)
            }
        }
    }
}