package com.avilanii.backttend.services

import com.avilanii.backttend.domain.models.Event
import com.avilanii.backttend.infrastructure.repo.EventRepositoryImpl

class EventService(
    private val eventRepository: EventRepositoryImpl,
) {
    suspend fun getAllEvents(): List<Event>{
        return eventRepository.getAllEvents()
    }

    suspend fun addEvent(event: Event): Int? {
        return eventRepository.addEvent(event)
    }

    suspend fun getEventById(id: Int): Event? {
        return eventRepository.getEvent(id)
    }
}