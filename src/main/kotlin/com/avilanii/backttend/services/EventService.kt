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

    suspend fun getEventById(userId: Int, eventId: Int): Event? {
        return eventRepository.getEvent(eventId)
    }

    suspend fun getEventRole(userId: Int, eventId: Int): ParticipantRole? {
        return eventRepository.getEventRole(userId, eventId)
    }
}