package com.avilanii.backttend.services

import com.avilanii.backttend.domain.models.AttendeeTier
import com.avilanii.backttend.infrastructure.datatransferobjects.SmartGateWithCountDTO
import com.avilanii.backttend.infrastructure.repo.SmartGateRepositoryImpl

class SmartGateService(
    private val smartGateRepository: SmartGateRepositoryImpl
) {
    suspend fun getSmartGates(eventId: Int): List<SmartGateWithCountDTO>{
        return smartGateRepository.getSmartGates(eventId)
    }
    suspend fun addSmartGate(eventId: Int, name: String): String?{
        return smartGateRepository.addSmartGate(eventId, name)
    }
    suspend fun addSmartGateTier(eventId: Int, name: String, attendeeTier: AttendeeTier): Int{
        return addSmartGateTier(eventId, name, attendeeTier)
    }
    suspend fun removeSmartGateTier(eventId: Int, name: String, attendeeTier: AttendeeTier): Int{
        return removeSmartGateTier(eventId, name, attendeeTier)
    }
}