package com.attend.com.attend.backttend.domain.repo

import com.attend.com.attend.backttend.domain.models.Event

interface EventRepository {
    suspend fun getAllEvents(): List<Event>
    suspend fun getEvent(id: Int): Event?
    suspend fun addEvent(event: Event): Boolean
}