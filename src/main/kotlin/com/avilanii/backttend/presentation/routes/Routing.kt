package com.avilanii.backttend.presentation.routes

import com.avilanii.backttend.services.AnnouncementService
import com.avilanii.backttend.services.EventService
import com.avilanii.backttend.services.ParticipantService
import com.avilanii.backttend.services.QrCodeService
import com.avilanii.backttend.services.SmartGateService
import com.avilanii.backttend.services.UserService
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val eventService by inject<EventService>()
    val participantService by inject<ParticipantService>()
    val userService by inject<UserService>()
    val qrService by inject<QrCodeService>()
    val smartGateService by inject<SmartGateService>()
    val announcementService by inject<AnnouncementService>()
    routing {
        authRoutes(userService, participantService)
        usersRoutes(userService)
        eventsRoutes(eventService, participantService, userService, smartGateService, announcementService)
        participantsRoutes(participantService, userService)
        qrCodeRoutes(qrService)
        smartGateRoutes(smartGateService)
    }
}
