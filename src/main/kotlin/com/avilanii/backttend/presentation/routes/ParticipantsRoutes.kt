package com.avilanii.backttend.presentation.routes

import com.avilanii.backttend.domain.models.Participant
import com.avilanii.backttend.infrastructure.datatransferobjects.toParticipantResponseDTO
import com.avilanii.backttend.services.ParticipantService
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.participantsRoutes(
    participantService: ParticipantService
) {
    route("/events/{id}/participants") {
        get {
            val id = call.parameters["id"] ?: return@get call.respondText("No id provided", status = HttpStatusCode.NotFound)
            val participants = participantService.getAll(id.toInt())
            call.respond(HttpStatusCode.OK, participants.toParticipantResponseDTO())
        }

        post<Participant> { participant ->
            TODO("participant joinDate should be computed server-side, can be null -- change model")
            val participantId = participantService.addParticipant(participant)
            val addedParticipant = participantService.getParticipant(participantId) ?: return@post call.respondText("Participant not created", status = HttpStatusCode.BadRequest)
            call.respond(HttpStatusCode.OK, addedParticipant)
        }
    }
}