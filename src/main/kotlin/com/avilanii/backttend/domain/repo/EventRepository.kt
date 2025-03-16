package com.avilanii.backttend.domain.repo

import com.avilanii.backttend.domain.models.Event

interface EventRepository {
    suspend fun getAllEvents(userId: Int): List<Event>
    suspend fun getEvent(eventId: Int): Event?
    suspend fun addEvent(event: Event): Int
}