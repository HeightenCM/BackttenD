package com.avilanii.backttend.presentation.routes

import com.avilanii.backttend.domain.models.Participant
import com.avilanii.backttend.infrastructure.datatransferobjects.toParticipantResponseDTO
import com.avilanii.backttend.services.ParticipantService
import com.avilanii.backttend.services.UserService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

fun Route.participantsRoutes(
    participantService: ParticipantService,
    userService: UserService,
) {
    authenticate("auth-jwt") {
        route("/events/{id}/participants") {
            get {
                val id = call.parameters["id"] ?: return@get call.respondText("No id provided", status = HttpStatusCode.NotFound)
                val participants = participantService.getAll(id.toInt())
                call.respond(HttpStatusCode.OK, participants.toParticipantResponseDTO())
            }

            post<Participant> { participant ->
                val eventId = call.parameters["id"] ?: return@post call.respondText("No id provided", status = HttpStatusCode.NotFound)
                val userId = userService.findByEmail(participant.email)?.id
                val participantId = participantService.addParticipant(participant.copy(eventId = eventId.toInt(), userId = userId, qrCode = UUID.randomUUID().toString()))
                val addedParticipant = participantService.getParticipantById(participantId) ?: return@post call.respondText("Participant not created", status = HttpStatusCode.BadRequest)
                call.respond(HttpStatusCode.OK, addedParticipant)
            }
        }
    }
}