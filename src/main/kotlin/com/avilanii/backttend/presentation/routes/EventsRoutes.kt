package com.avilanii.backttend.presentation.routes

import com.avilanii.backttend.domain.models.AttendeeTier
import com.avilanii.backttend.domain.models.Event
import com.avilanii.backttend.domain.models.Participant
import com.avilanii.backttend.domain.models.ParticipantRole
import com.avilanii.backttend.domain.models.ParticipantStatus
import com.avilanii.backttend.infrastructure.datatransferobjects.toEventsResponseDTO
import com.avilanii.backttend.services.EventService
import com.avilanii.backttend.services.ParticipantService
import com.avilanii.backttend.services.SmartGateService
import com.avilanii.backttend.services.UserService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.receive
import io.ktor.server.request.receiveText
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

fun Route.eventsRoutes(
    eventService: EventService,
    participantService: ParticipantService,
    userService: UserService,
    smartGateService: SmartGateService
) {
    authenticate("auth-jwt"){
        route("/events") {
            get {
                val userId = call.principal<JWTPrincipal>()?.payload?.subject!!.toInt()
                val role = call.principal<JWTPrincipal>()?.payload?.getClaim("role")?.`as`(ParticipantRole::class.java)?:ParticipantRole.ORGANIZER
                val events = eventService.getAllEvents(userId, role)
                call.respond(HttpStatusCode.OK, events.toEventsResponseDTO())
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
                    qrCode = "ORGANIZER"
                ))
                call.respond(HttpStatusCode.OK, createdEvent)
            }

            route("/{id}") {
                get {
                    val userId = call.principal<JWTPrincipal>()?.payload?.subject!!.toInt()
                    val eventId = call.parameters["id"]?.toIntOrNull() ?: return@get call.respondText("Invalid id", status = HttpStatusCode.BadRequest)
                    val event = eventService.getEventById(userId, eventId, listOf(ParticipantRole.ORGANIZER)) ?: return@get call.respondText("Event not found or access denied", status = HttpStatusCode.NotFound)
                    call.respond(HttpStatusCode.OK, event)
                }

                get("/tiers") {
                    val userId = call.principal<JWTPrincipal>()?.payload?.subject!!.toInt()
                    val eventId = call.parameters["id"]?.toIntOrNull() ?: return@get call.respondText("Invalid id", status = HttpStatusCode.BadRequest)
                    val tiers = participantService.getAllEventTiers(userId, eventId)
                    call.respond(HttpStatusCode.OK, tiers)
                }

                post("/tiers") {
                    val userId = call.principal<JWTPrincipal>()?.payload?.subject!!.toInt()
                    val eventId = call.parameters["id"]?.toIntOrNull() ?: return@post call.respondText("Invalid id", status = HttpStatusCode.BadRequest)
                    val tier = call.receive<AttendeeTier>()
                    participantService.addEventTier(userId, eventId, tier.title)
                    call.respond(HttpStatusCode.OK)
                }

                delete("/tiers") {
                    val userId = call.principal<JWTPrincipal>()?.payload?.subject!!.toInt()
                    val eventId = call.parameters["id"]?.toIntOrNull() ?: return@delete call.respondText("Invalid id", status = HttpStatusCode.BadRequest)
                    val tier = call.receive<AttendeeTier>()
                    participantService.removeEventTier(userId, eventId, tier.title)
                    call.respond(HttpStatusCode.OK)
                }

                route("/smartGates") {
                    get{
                        val eventId = call.parameters["id"]?.toIntOrNull() ?: return@get call.respondText("Invalid id", status = HttpStatusCode.BadRequest)
                        val smartGates = smartGateService.getSmartGates(eventId)
                        call.respond(message = smartGates,  status = HttpStatusCode.OK)
                    }
                    post {
                        val eventId = call.parameters["id"]?.toIntOrNull() ?: return@post call.respondText("Invalid id", status = HttpStatusCode.BadRequest)
                        val name = call.receiveText()
                        val smartGate = smartGateService.addSmartGate(eventId, name)
                        call.respond(message = smartGate, status = HttpStatusCode.OK)
                    }
                    route("/{gateId}"){
                        get("/activate"){
                            val eventId = call.parameters["id"]?.toIntOrNull() ?: return@get call.respondText("Invalid id", status = HttpStatusCode.BadRequest)
                            val gateId = call.parameters["gateId"]?.toIntOrNull() ?: return@get call.respondText("Invalid id", status = HttpStatusCode.BadRequest)
                            val uniqueIdentifier = smartGateService.activateSmartGate(eventId, gateId)
                            call.respond( message = uniqueIdentifier, status = HttpStatusCode.OK)
                        }
                        route("/tiers") {
                            get{
                                val eventId = call.parameters["id"]?.toIntOrNull() ?: return@get call.respondText("Invalid id", status = HttpStatusCode.BadRequest)
                                val gateId = call.parameters["gateId"]?.toIntOrNull() ?: return@get call.respondText("Invalid id", status = HttpStatusCode.BadRequest)
                                val tiers = smartGateService.getGateTiers(eventId, gateId)
                                call.respond(message = tiers, status = HttpStatusCode.OK)
                            }
                            put("{tierId}") {
                                val gateId = call.parameters["gateId"]?.toIntOrNull() ?: return@put call.respondText("Invalid id", status = HttpStatusCode.BadRequest)
                                val tierId = call.parameters["tierId"]?.toIntOrNull() ?: return@put call.respondText("Invalid id", status = HttpStatusCode.BadRequest)
                                smartGateService.changeGateTierState(gateId, tierId)
                                call.respond(HttpStatusCode.OK)
                            }
                        }
                    }
                }
            }

            route("/attending") {
                get {
                    val userId = call.principal<JWTPrincipal>()?.payload?.subject!!.toInt()
                    val events = eventService.getAllEvents(userId, ParticipantRole.ATTENDEE)
                    call.respond(HttpStatusCode.OK, events.toEventsResponseDTO())
                }

                post<String> { qrValue ->
                    val userId = call.principal<JWTPrincipal>()?.payload?.subject!!.toInt()
                    val eventAttendingAdded = eventService.getEventByQr(qrValue)
                    if(eventAttendingAdded != null){
                        if(!participantService.checkParticipantEnrollment(userId, eventAttendingAdded.id!!)) {
                            userService.findById(userId)?.let { user ->
                                participantService.addParticipant(
                                    Participant(
                                        eventId = eventAttendingAdded.id,
                                        userId = userId,
                                        name = user.name,
                                        email = user.email,
                                        status = ParticipantStatus.ACCEPTED,
                                        role = ParticipantRole.ATTENDEE,
                                        qrCode = UUID.randomUUID().toString()
                                    )
                                )
                            }
                            call.respond(HttpStatusCode.Created, eventAttendingAdded)
                        } else call.respond(HttpStatusCode.BadRequest, "Already enrolled for event.")
                    } else call.respond(HttpStatusCode.NotFound, "No event found or QR expired." )
                }

                post("/{id}"){
                    val userId = call.principal<JWTPrincipal>()?.payload?.subject!!.toInt()
                    val eventId = call.parameters["id"]?.toIntOrNull() ?: return@post call.respondText("No id provided", status = HttpStatusCode.BadRequest)
                    val isAccepted = call.receive<Boolean>()
                    val status = if (isAccepted) ParticipantStatus.ACCEPTED else ParticipantStatus.REJECTED
                    participantService.updateParticipantStatus(userId, eventId, status)
                    call.respond(HttpStatusCode.OK)
                }
            }
        }
    }
}