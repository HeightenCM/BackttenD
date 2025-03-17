package com.avilanii.backttend.presentation.routes

import com.avilanii.backttend.services.QrCodeService
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.qrCodeRoutes(
    qrCodeService: QrCodeService
) {
    authenticate("auth-jwt") {
        route("qr"){
            get {

            }
        }
    }
}