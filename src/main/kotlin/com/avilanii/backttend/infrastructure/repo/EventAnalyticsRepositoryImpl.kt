package com.avilanii.backttend.infrastructure.repo

import com.avilanii.backttend.domain.models.AttendeeTier
import com.avilanii.backttend.domain.models.ParticipantStatus
import com.avilanii.backttend.domain.repo.AnalyticsRepository
import com.avilanii.backttend.infrastructure.database.EventTierTable
import com.avilanii.backttend.infrastructure.database.ParticipantTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class EventAnalyticsRepositoryImpl(
    private val database: Database
): AnalyticsRepository {
    override suspend fun getTierPie(eventId: Int): List<Pair<String, Int>> =
        transaction(database) {
            val tiers = EventTierTable
                .selectAll()
                .where { EventTierTable.eventId eq eventId }
                .map { AttendeeTier(
                    id = it[EventTierTable.id].value,
                    title = it[EventTierTable.title]
                ) }
            tiers.map { tier ->
                val count = ParticipantTable
                    .selectAll()
                    .where { ParticipantTable.tierId eq tier.id }
                    .count()
                Pair(tier.title, count.toInt())
            }
        }

    override suspend fun getStatusPie(eventId: Int): List<Pair<ParticipantStatus, Int>> =
        transaction(database){
            ParticipantTable
                .selectAll()
                .where { ParticipantTable.eventId eq eventId }
                .groupingBy { it[ParticipantTable.status] }
                .eachCount()
                .toList()
        }
}