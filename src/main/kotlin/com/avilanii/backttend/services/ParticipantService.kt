package com.avilanii.backttend.services

import com.avilanii.backttend.domain.models.Participant
import com.avilanii.backttend.domain.models.ParticipantStatus
import com.avilanii.backttend.infrastructure.repo.ParticipantRepositoryImpl

class ParticipantService(
    private val participantRepository: ParticipantRepositoryImpl
) {
    suspend fun getAll(eventId: Int): List<Participant> {
        return participantRepository.getAllParticipants(eventId)
    }

    suspend fun getParticipantByUserId(userId: Int): Participant? {
        return participantRepository.getParticipantByUserId(userId)
    }

    suspend fun getParticipantById(id: Int): Participant? {
        return participantRepository.getParticipantById(id)
    }

    suspend fun addParticipant(participant: Participant): Int {
        return participantRepository.addParticipant(participant)
    }

    suspend fun updateUserIds(userId: Int, userEmail: String) {
        return participantRepository.updateUserId(userId, userEmail)
    }

    suspend fun updateParticipantStatus(userId: Int, eventId: Int, status: ParticipantStatus): Int {
        return participantRepository.updateParticipantStatus(userId, eventId, status)
    }
}