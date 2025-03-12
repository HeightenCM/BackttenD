package com.avilanii.backttend.infrastructure.repo

import com.avilanii.backttend.domain.models.Event
import com.avilanii.backttend.domain.repo.EventRepository

class EventRepositoryImpl: EventRepository {
    override suspend fun getAllEvents(): List<Event> {
        TODO("Not yet implemented")
    }

    override suspend fun getEvent(id: Int): Event? {
        TODO("Not yet implemented")
    }

    override suspend fun addEvent(event: Event): Boolean {
        TODO("Not yet implemented")
    }
}