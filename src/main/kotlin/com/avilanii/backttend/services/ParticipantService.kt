package com.avilanii.backttend.services

import com.avilanii.backttend.domain.models.AttendeeTier
import com.avilanii.backttend.domain.models.Participant
import com.avilanii.backttend.domain.models.ParticipantInteraction
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

    suspend fun checkParticipantEnrollment(userId: Int, eventId: Int): Boolean {
        return participantRepository.checkParticipationEnrollment(userId, eventId)
    }

    suspend fun checkInParticipant(eventId: Int, scannedQr: String): Boolean? {
        return participantRepository.checkParticipantEnrollmentByQr(eventId, scannedQr)
            ?.let{
                participantRepository
                    .checkInCheckOutParticipant(it, ParticipantInteraction.CHECK_IN)
            } // null if non-existent participant
    }

    suspend fun checkOutParticipant(eventId: Int, scannedQr: String): Boolean? {
        return participantRepository.checkParticipantEnrollmentByQr(eventId, scannedQr)
            ?.let {
                participantRepository
                    .checkInCheckOutParticipant(it, ParticipantInteraction.CHECK_OUT)
            } // null if non-existent participant
    }

    suspend fun getAllEventTiers(organizerId: Int, eventId: Int): List<AttendeeTier>{
        return participantRepository.getAllEventTiers(organizerId, eventId)
    }

    suspend fun addEventTier(organizerId: Int, eventId: Int, eventTier: String): Int {
        return participantRepository.addEventTier(organizerId, eventId, eventTier)
    }

    suspend fun removeEventTier(organizerId: Int, eventId: Int, eventTier: String): Int {
        return participantRepository.deleteEventTier(organizerId, eventId, eventTier);
    }
}