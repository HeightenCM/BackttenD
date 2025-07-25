package com.avilanii.backttend.domain.repo

import com.avilanii.backttend.domain.models.AttendeeTier
import com.avilanii.backttend.domain.models.ParticipantStatus
import com.avilanii.backttend.domain.models.SmartGate

interface SmartGateRepository {
    suspend fun getSmartGates(eventId: Int): List<SmartGate>
    suspend fun addSmartGate(eventId: Int, name: String): SmartGate
    suspend fun changeGateTierState(gateId: Int, tierId: Int): Int
    suspend fun activateSmartGate(eventId: Int, gateId: Int): String
    suspend fun getGateTiers(eventId: Int, gateId: Int): List<AttendeeTier>
    suspend fun getGateId(gateIdentifier: String): Int?
    suspend fun getAttendeeByQr(attendeeIdentifier: String): Triple<Int, ParticipantStatus, Int?>?
    suspend fun checkInAttendee(gateId: Int, attendeeId: Int, tierId: Int): Boolean
}