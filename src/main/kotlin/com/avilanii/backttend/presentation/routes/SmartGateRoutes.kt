package com.avilanii.backttend.presentation.routes

import com.avilanii.backttend.domain.models.AttendeeTier
import com.avilanii.backttend.services.SmartGateService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.request.receiveText
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.smartGateRoutes(
    smartGateService: SmartGateService,
) {
    authenticate("auth-jwt"){
        route("smartGate/{id}") {
            get{
                val userId = call.principal<JWTPrincipal>()?.payload?.subject!!.toInt()
                val eventId = call.parameters["id"]?.toIntOrNull() ?: return@get call.respondText("Invalid id", status = HttpStatusCode.BadRequest)
                val smartGates = smartGateService.getSmartGates(eventId)
                call.respond(message = smartGates,  status = HttpStatusCode.OK)
            }
            post {
                val eventId = call.parameters["id"]?.toIntOrNull() ?: return@post call.respondText("Invalid id", status = HttpStatusCode.BadRequest)
                val name = call.receiveText()
                val uniqueRandId = smartGateService.addSmartGate(eventId, name)
                if(uniqueRandId != null){
                    call.respondText(uniqueRandId, status = HttpStatusCode.OK)
                }
                call.respond(HttpStatusCode.BadRequest)
            }
            route("/tier"){
                post {
                    val eventId = call.parameters["id"]?.toIntOrNull() ?: return@post call.respondText("Invalid id", status = HttpStatusCode.BadRequest)
                    val nameAndTier: Pair<String, AttendeeTier> = call.receive()
                    smartGateService.addSmartGateTier(eventId, nameAndTier.first, nameAndTier.second)
                    call.respond(HttpStatusCode.OK)
                }
                delete {
                    val eventId = call.parameters["id"]?.toIntOrNull() ?: return@delete call.respondText("Invalid id", status = HttpStatusCode.BadRequest)
                    val nameAndTier: Pair<String, AttendeeTier> = call.receive()
                    smartGateService.removeSmartGateTier(eventId, nameAndTier.first, nameAndTier.second)
                    call.respond(HttpStatusCode.OK)
                }
            }
        }
    }
}