package com.avilanii.backttend.presentation.routes

import com.avilanii.backttend.domain.models.ParticipantStatus
import com.avilanii.backttend.services.SmartGateService
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receiveText
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route

fun Route.smartGateRoutes(
    smartGateService: SmartGateService
) {
    route("/smartGates"){
        post{
            val gateIdentifier = call.receiveText()
            smartGateService.getGateId(gateIdentifier)?.let { gateId ->
                call.respond(message = gateId, status = HttpStatusCode.OK)
            } ?: call.respond(HttpStatusCode.BadRequest)
        }
        put("/{id}"){
            val identifiers = call.receiveText().split("::")
            print(identifiers[0] + "::" + identifiers[1])
            val gateId = call.parameters["id"]?.toIntOrNull() ?: return@put call.respondText("Invalid id.", status = HttpStatusCode.BadRequest)
            smartGateService.getGateId(identifiers[1])?.let { receivedId ->
                if (gateId != receivedId)
                    return@put call.respond(
                        message = "Gate identifiers don't match.",
                        status = HttpStatusCode.BadRequest
                    )
                val attendee = smartGateService.getAttendeeByQr(identifiers[0])
                attendee?.let {
                    if (attendee.second == ParticipantStatus.CHECKED_IN)
                        return@put call.respond(
                            message = "Attendee already checked in.",
                            status = HttpStatusCode.BadRequest
                        )
                    if (smartGateService.checkInAttendee(gateId, attendee.first, attendee.third!!))
                        return@put call.respond(
                            message = "Attendee is good to go.",
                            status = HttpStatusCode.OK
                        )
                    else
                        call.respond(
                            message = "Tier not permitted here.",
                            status = HttpStatusCode.BadRequest
                        )
                } ?: call.respond(message = "Attendee doesn't exist.", status = HttpStatusCode.BadRequest)
            } ?: call.respond(
                message = "Invalid gate identifier.",
                status = HttpStatusCode.BadRequest
            )
        }
    }
}