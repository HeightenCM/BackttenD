package com.avilanii.backttend.presentation.routes

import com.avilanii.backttend.domain.models.AttendeeTier
import com.avilanii.backttend.domain.models.Participant
import com.avilanii.backttend.infrastructure.datatransferobjects.CheckInConfirmationDTO
import com.avilanii.backttend.infrastructure.datatransferobjects.toParticipantResponseDTO
import com.avilanii.backttend.services.ParticipantService
import com.avilanii.backttend.services.UserService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.receive
import io.ktor.server.request.receiveText
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
                val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respondText("No id provided", status = HttpStatusCode.NotFound)
                val participants = participantService.getAll(id)
                call.respond(HttpStatusCode.OK, participants.toParticipantResponseDTO())
            }

            post<Participant> { participant ->
                val eventId = call.parameters["id"]?.toIntOrNull() ?: return@post call.respondText("No id provided", status = HttpStatusCode.NotFound)
                val userId = userService.findByEmail(participant.email)?.id
                val participantId = participantService.addParticipant(participant.copy(eventId = eventId, userId = userId, qrCode = UUID.randomUUID().toString()))
                val addedParticipant = participantService.getParticipantById(participantId) ?: return@post call.respondText("Participant not created", status = HttpStatusCode.BadRequest)
                call.respond(HttpStatusCode.OK, addedParticipant)
            }

            get("/checkin"){
                val eventId = call.parameters["id"]?.toIntOrNull() ?: return@get call.respondText("No id provided", status = HttpStatusCode.NotFound)
                val scannedQr = call.receiveText()
                val checkInResult = participantService.checkInParticipant(eventId, scannedQr)
                checkInResult?.let {
                    if (checkInResult) {
                        call.respond(HttpStatusCode.OK,
                            CheckInConfirmationDTO(
                                "The participant is good to go.",
                                true))
                    } else call.respond(HttpStatusCode.OK,
                        CheckInConfirmationDTO(
                            "The participant has already checked in!",
                            false))
                } ?: call.respondText("Invalid QR Code!", status = HttpStatusCode.BadRequest)
            }

            get("/checkout"){
                val eventId = call.parameters["id"]?.toIntOrNull() ?: return@get call.respondText("No id provided", status = HttpStatusCode.NotFound)
                val scannedQr = call.receiveText()
                val checkOutResult = participantService.checkOutParticipant(eventId, scannedQr)
                checkOutResult?.let {
                    if (checkOutResult) {
                        call.respond(HttpStatusCode.OK,
                            CheckInConfirmationDTO(
                                "The participant is free to leave.",
                                true))
                    } else call.respond(HttpStatusCode.OK,
                        CheckInConfirmationDTO(
                            "The participant hasn't checked in already!",
                            false))
                } ?: call.respondText("Invalid QR Code!", status = HttpStatusCode.BadRequest)
            }

            route("/tiers") {
                post<Pair<Participant, AttendeeTier>> { pair ->
                    val userId = call.parameters["id"]?.toIntOrNull() ?: return@post call.respondText("No id provided", status = HttpStatusCode.NotFound)
                    // ??? above might be wrong.. idk
                    val participant = pair.first
                    val attendeeTier = pair.second
                    if(participantService.assignParticipantTier(userId, participant, attendeeTier)){
                        call.respond(HttpStatusCode.OK)
                    } else call.respond(HttpStatusCode.BadRequest)
                }

                delete {
                    val userId = call.parameters["id"]?.toIntOrNull() ?: return@delete call.respondText("No id provided", status = HttpStatusCode.NotFound)
                    val participant = call.receive<Participant>()
                    if(participantService.removeParticipantTier(userId, participant) == 1)
                        call.respondText("Success", status = HttpStatusCode.OK)
                    else
                        call.respondText("Participant not found!", status = HttpStatusCode.NotFound)
                }
            }

            post("/nfc"){
                val eventId = call.parameters["id"]?.toIntOrNull() ?: return@post call.respondText("No id provided", status = HttpStatusCode.NotFound)
                
            }
        }
    }
}