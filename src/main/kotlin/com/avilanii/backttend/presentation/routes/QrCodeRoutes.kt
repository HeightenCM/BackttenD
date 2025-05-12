package com.avilanii.backttend.presentation.routes

import com.avilanii.backttend.domain.models.ExternalQR
import com.avilanii.backttend.services.QrCodeService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.response.respond
import io.ktor.server.routing.*

fun Route.qrCodeRoutes(
    qrCodeService: QrCodeService
) {
    authenticate("auth-jwt") {
        route("qr"){
            get("/{eventId}") {
                val userId = call.principal<JWTPrincipal>()?.payload?.subject!!.toInt()
                val eventId = call.parameters["eventId"]?.toIntOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest, "Bad request")
                val qrCode = qrCodeService.getQrCode(eventId, userId) ?: return@get call.respond(HttpStatusCode.BadRequest, "Bad request")
                call.respond(HttpStatusCode.OK, qrCode)
            }
            get("/invite/{eventId}") {
                val userId = call.principal<JWTPrincipal>()?.payload?.subject!!.toInt()
                val eventId = call.parameters["eventId"]?.toIntOrNull() ?: return@get call.respond(status = HttpStatusCode.BadRequest, "Bad request")
                val qrCode = qrCodeService.getTimeLimitedQrCode(eventId, userId)
                call.respond(HttpStatusCode.OK, qrCode)
            }
            get {
                val userId = call.principal<JWTPrincipal>()?.payload?.subject!!.toInt()
                val qrCodes = qrCodeService.getAllQrCodes(userId)
                call.respond(HttpStatusCode.OK, qrCodes)

            }
            post<ExternalQR> { externalQr ->
                val userId = call.principal<JWTPrincipal>()?.payload?.subject!!.toInt()
                val registeredQr = qrCodeService.registerExternalQrCode(userId, externalQr)
                call.respond(HttpStatusCode.Created, registeredQr)
            }
        }
    }
}