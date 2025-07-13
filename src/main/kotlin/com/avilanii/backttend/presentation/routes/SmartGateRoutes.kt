package com.avilanii.backttend.presentation.routes

import com.avilanii.backttend.services.SmartGateService
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Route

fun Route.smartGateRoutes(
    smartGateService: SmartGateService
) {
    authenticate("auth-jwt"){

    }
}