package com.avilanii.backttend.domain.repo

import com.avilanii.backttend.domain.models.Event

interface EventRepository {
    suspend fun getAllEvents(): List<Event>
    suspend fun getEvent(id: Int): Event?
    suspend fun addEvent(event: Event): Event?
}