package com.avilanii.backttend.services

import com.avilanii.backttend.domain.models.AttendeeTier
import com.avilanii.backttend.domain.models.ParticipantStatus
import com.avilanii.backttend.domain.models.SmartGate
import com.avilanii.backttend.infrastructure.repo.SmartGateRepositoryImpl

class SmartGateService(
    private val smartGateRepository: SmartGateRepositoryImpl
) {
    suspend fun getSmartGates(eventId: Int): List<SmartGate>{
        return smartGateRepository.getSmartGates(eventId)
    }
    suspend fun addSmartGate(eventId: Int, name: String): SmartGate{
        return smartGateRepository.addSmartGate(eventId, name)
    }
    suspend fun changeGateTierState(gateId: Int, tierId: Int): Int{
        return smartGateRepository.changeGateTierState(gateId, tierId)
    }
    suspend fun activateSmartGate(eventId: Int, gateId: Int): String{
        return smartGateRepository.activateSmartGate(eventId, gateId)
    }
    suspend fun getGateTiers(eventId: Int, gateId: Int): List<AttendeeTier>{
        return smartGateRepository.getGateTiers(eventId, gateId)
    }
    suspend fun getGateId(gateIdentifier: String): Int?{
        return smartGateRepository.getGateId(gateIdentifier)
    }
    suspend fun getAttendeeByQr(attendeeIdentifier: String): Triple<Int, ParticipantStatus, Int?>?{
        return smartGateRepository.getAttendeeByQr(attendeeIdentifier)
    }
    suspend fun checkInAttendee(gateId: Int, attendeeId: Int, tierId: Int): Boolean{
        return smartGateRepository.checkInAttendee(gateId, attendeeId, tierId)
    }
}