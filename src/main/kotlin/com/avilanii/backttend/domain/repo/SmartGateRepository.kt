package com.avilanii.backttend.domain.repo

import com.avilanii.backttend.domain.models.AttendeeTier
import com.avilanii.backttend.infrastructure.datatransferobjects.SmartGateWithCountDTO

interface SmartGateRepository {
    suspend fun getSmartGates(eventId: Int): List<SmartGateWithCountDTO>
    suspend fun addSmartGate(eventId: Int, name: String): String?
    suspend fun addSmartGateTier(eventId: Int, name: String, attendeeTier: AttendeeTier): Int
    suspend fun removeSmartGateTier(eventId: Int, name: String, attendeeTier: AttendeeTier): Int
}