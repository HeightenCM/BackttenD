package com.avilanii.backttend.domain.repo

import com.avilanii.backttend.domain.models.Event
import com.avilanii.backttend.domain.models.ParticipantRole

interface EventRepository {
    suspend fun getAllEvents(userId: Int, participantRole: ParticipantRole): List<Event>
    suspend fun getEvent(eventId: Int): Event?
    suspend fun addEvent(event: Event): Int
    suspend fun checkEventPermission(userId: Int, eventId: Int, requiredRole: List<ParticipantRole>): Boolean
    suspend fun getEventRole(userId: Int, eventId: Int): ParticipantRole?
    suspend fun getEventByQr(qrCode: String): Event?
}