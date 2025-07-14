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

    suspend fun getAllEventTiers(eventId: Int): List<AttendeeTier>{
        return participantRepository.getAllEventTiers(eventId)
    }

    suspend fun addEventTier(eventId: Int, eventTier: String): Int {
        return participantRepository.addEventTier(eventId, eventTier)
    }

    suspend fun removeEventTier(eventId: Int, tierId: Int) {
        return participantRepository.deleteEventTier(eventId, tierId)
    }

    suspend fun assignParticipantTier(eventId: Int, participantId: Int, tierId: Int): String{
        return participantRepository.assignParticipantTier(eventId, participantId, tierId)
    }

    suspend fun removeParticipantTier(eventId: Int, participantId: Int): Int{
        return participantRepository.removeParticipantTier(eventId, participantId)
    }
}