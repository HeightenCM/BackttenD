package com.avilanii.backttend.domain.repo

import com.avilanii.backttend.domain.models.AttendeeTier
import com.avilanii.backttend.domain.models.Participant
import com.avilanii.backttend.domain.models.ParticipantInteraction
import com.avilanii.backttend.domain.models.ParticipantStatus

interface ParticipantRepository {
    suspend fun getAllParticipants(eventId: Int): List<Participant>
    suspend fun getParticipantByUserId(userId: Int): Participant?
    suspend fun getParticipantById(id: Int): Participant?
    suspend fun addParticipant(participant: Participant): Int
    suspend fun updateUserId(userId: Int, userEmail: String)
    suspend fun updateParticipantStatus(userId: Int, eventId: Int, status: ParticipantStatus): Int
    suspend fun checkParticipationEnrollment(userId: Int, eventId: Int): Boolean
    suspend fun checkParticipantEnrollmentByQr(eventId: Int, qrCode: String): Int?
    suspend fun checkInCheckOutParticipant(participantId: Int, interaction: ParticipantInteraction): Boolean
    suspend fun addEventTier(eventId: Int, tier: String): Int
    suspend fun deleteEventTier(eventId: Int, tierId: Int)
    suspend fun getAllEventTiers(eventId: Int) : List<AttendeeTier>
    suspend fun assignParticipantTier(eventId: Int, participantId: Int, tierId: Int): String
    suspend fun removeParticipantTier(eventId: Int, participantId: Int): Int
}