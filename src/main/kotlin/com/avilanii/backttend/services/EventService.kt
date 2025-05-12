package com.avilanii.backttend.services

import com.avilanii.backttend.domain.models.Event
import com.avilanii.backttend.domain.models.ParticipantRole
import com.avilanii.backttend.infrastructure.repo.EventRepositoryImpl

class EventService(
    private val eventRepository: EventRepositoryImpl,
) {
    suspend fun getAllEvents(userId: Int, participantRole: ParticipantRole): List<Event>{
        return eventRepository.getAllEvents(
            userId,
            participantRole = participantRole
        )
    }

    suspend fun addEvent(event: Event): Int {
        return eventRepository.addEvent(event)
    }

    suspend fun getEventById(userId: Int, eventId: Int, roles: List<ParticipantRole>): Event? {
        if (roles.isEmpty()) return eventRepository.getEvent(eventId)
        else {
            val isAllowed = eventRepository.checkEventPermission(userId, eventId, roles)
            return if(isAllowed)
                eventRepository.getEvent(eventId)
            else
                null
        }

    }

    suspend fun getEventRole(userId: Int, eventId: Int): ParticipantRole? {
        return eventRepository.getEventRole(userId, eventId)
    }

    suspend fun getEventByQr(qrCode: String): Event? {
        return eventRepository.getEventByQr(qrCode)
    }
}