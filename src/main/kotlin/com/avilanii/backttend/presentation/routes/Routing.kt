package com.avilanii.backttend.presentation.routes

import com.avilanii.backttend.services.EventService
import com.avilanii.backttend.services.ParticipantService
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val eventService by inject<EventService>()
    val participantService by inject<ParticipantService>()
    routing {
        eventsRoutes(eventService)
        participantsRoutes(participantService)
    }
}
