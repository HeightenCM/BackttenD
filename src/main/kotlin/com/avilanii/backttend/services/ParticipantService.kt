package com.avilanii.backttend.services

import com.avilanii.backttend.domain.models.Participant
import com.avilanii.backttend.infrastructure.repo.ParticipantRepositoryImpl

class ParticipantService(
    private val participantRepository: ParticipantRepositoryImpl
) {
    suspend fun getAll(eventId: Int): List<Participant> {
        return participantRepository.getAllParticipants(eventId)
    }

    suspend fun getParticipant(id: Int): Participant? {
        return participantRepository.getParticipant(id)
    }

    suspend fun addParticipant(participant: Participant): Int {
        return participantRepository.addParticipant(participant)
    }
}