package com.avilanii.backttend.domain.repo

import com.avilanii.backttend.domain.models.ParticipantStatus

interface AnalyticsRepository {
    suspend fun getTierPie(eventId: Int): List<Pair<String,Int>>
    suspend fun getStatusPie(eventId: Int): List<Pair<ParticipantStatus,Int>>
}