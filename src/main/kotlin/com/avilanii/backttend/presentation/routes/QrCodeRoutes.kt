package com.avilanii.backttend.presentation.routes

import com.avilanii.backttend.services.QrCodeService
import io.ktor.server.routing.*

fun Route.qrCodeRoutes(
    qrCodeService: QrCodeService
) {
    route("qr"){
        get {

        }
    }
}