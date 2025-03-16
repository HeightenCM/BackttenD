package com.avilanii.backttend.domain.repo

import com.avilanii.backttend.domain.models.Participant

interface ParticipantRepository {
    suspend fun getAllParticipants(eventId: Int): List<Participant>
    suspend fun getParticipant(id: Int): Participant?
    suspend fun addParticipant(participant: Participant): Int
}