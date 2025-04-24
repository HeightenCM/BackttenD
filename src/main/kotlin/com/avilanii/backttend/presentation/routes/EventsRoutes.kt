package com.avilanii.backttend.presentation.routes

import com.avilanii.backttend.domain.models.Event
import com.avilanii.backttend.domain.models.Participant
import com.avilanii.backttend.domain.models.ParticipantRole
import com.avilanii.backttend.domain.models.ParticipantStatus
import com.avilanii.backttend.infrastructure.datatransferobjects.toEventsResponseDTO
import com.avilanii.backttend.infrastructure.repo.UserRepositoryImpl
import com.avilanii.backttend.services.EventService
import com.avilanii.backttend.services.ParticipantService
import com.avilanii.backttend.services.UserService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.Database
import org.koin.ktor.ext.inject
import java.time.LocalDateTime

fun Route.eventsRoutes(
    eventService: EventService,
    participantService: ParticipantService,
    userService: UserService
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
                val event = eventService.getEventById(userId, eventId, listOf(ParticipantRole.ORGANIZER)) ?: return@get call.respondText("Event not found or access denied", status = HttpStatusCode.NotFound)
                call.respond(HttpStatusCode.OK, event)
            }

            post<Event>{ event->
                val userId = call.principal<JWTPrincipal>()?.payload?.subject!!.toInt()
                val createdEventId = eventService.addEvent(event)
                val createdEvent = eventService.getEventById(userId, createdEventId, emptyList()) ?: return@post call.respondText("Event not created", status = HttpStatusCode.BadRequest)
                val user = userService.findById(userId)!!
                participantService.addParticipant(Participant(
                    eventId = createdEventId,
                    userId = userId,
                    name = user.name,
                    email = user.email,
                    status = ParticipantStatus.ACCEPTED,
                    role = ParticipantRole.ORGANIZER,
                    joinDate = LocalDateTime.now().toString()
                ))
                call.respond(HttpStatusCode.OK, createdEvent)
            }

            get("/attending"){
                val userId = call.principal<JWTPrincipal>()?.payload?.subject!!.toInt()
                val events = eventService.getAllEvents(userId, ParticipantRole.ATTENDEE)
                call.respond(HttpStatusCode.OK, events.toEventsResponseDTO())
            }

            post("attending"){ //TODO("Required")
                val userId = call.principal<JWTPrincipal>()?.payload?.subject!!.toInt()
                call.respond(HttpStatusCode.Created, Unit)
            }

            post("/attending/{id}"){
                val userId = call.principal<JWTPrincipal>()?.payload?.subject!!.toInt()
                val eventId = call.parameters["id"]?.toIntOrNull() ?: return@post call.respondText("No id provided", status = HttpStatusCode.BadRequest)
                val isAccepted = call.receive<Boolean>()
                participantService.updateParticipantStatus(userId, eventId, isAccepted)
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}