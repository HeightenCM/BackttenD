package com.avilanii.backttend.domain.repo

import com.avilanii.backttend.domain.models.Participant
import com.avilanii.backttend.domain.models.ParticipantStatus

interface ParticipantRepository {
    suspend fun getAllParticipants(eventId: Int): List<Participant>
    suspend fun getParticipantByUserId(userId: Int): Participant?
    suspend fun getParticipantById(id: Int): Participant?
    suspend fun addParticipant(participant: Participant): Int
    suspend fun updateUserId(userId: Int, userEmail: String)
    suspend fun updateParticipantStatus(userId: Int, eventId: Int, status: ParticipantStatus): Int
    suspend fun checkParticipationEnrollment(userId: Int, eventId: Int): Boolean
}