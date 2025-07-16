package com.avilanii.backttend.services

import com.avilanii.backttend.infrastructure.repo.EventAnalyticsRepositoryImpl

class AnalyticsService(
    private val analyticsRepository: EventAnalyticsRepositoryImpl
) {
    suspend fun getTierPie(eventId: Int) : List<Pair<String,Int>>{
        return analyticsRepository.getTierPie(eventId)
    }
    suspend fun getStatusPie(eventId: Int) : List<Pair<String,Int>>{
        val distribution = analyticsRepository.getStatusPie(eventId)
        return distribution.map {
            it.first.name to it.second
        }
    }
}