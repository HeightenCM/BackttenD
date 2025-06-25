package com.avilanii.backttend.infrastructure.repo

import com.avilanii.backttend.domain.models.AttendeeTier
import com.avilanii.backttend.domain.repo.SmartGateRepository
import com.avilanii.backttend.infrastructure.database.EventTierTable
import com.avilanii.backttend.infrastructure.database.SmartGateTable
import com.avilanii.backttend.infrastructure.database.TierGatePermissionTable
import com.avilanii.backttend.infrastructure.datatransferobjects.SmartGateWithCountDTO
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

class SmartGateRepositoryImpl(
    private val database: Database,
): SmartGateRepository {
    override suspend fun getSmartGates(eventId: Int): List<SmartGateWithCountDTO> =
        transaction(database){
            SmartGateTable
                .selectAll()
                .where { SmartGateTable.eventId eq eventId }
                .map {
                    Triple(
                        it[SmartGateTable.id].value,
                        it[SmartGateTable.name],
                        it[SmartGateTable.eventId]
                    )
                }
                .map { gate ->
                    SmartGateWithCountDTO(
                        name = gate.second,
                        allowedTiers = TierGatePermissionTable
                            .innerJoin(EventTierTable)
                            .selectAll()
                            .where(TierGatePermissionTable.gateId eq gate.first)
                            .map {
                                Pair(
                                    AttendeeTier(
                                        title = it[EventTierTable.title]
                                    ),
                                    it[TierGatePermissionTable.count]
                                )
                            }
                    )
                }
        }

    override suspend fun addSmartGate(eventId: Int, name: String): String? =
        transaction(database){
            val id = SmartGateTable.insertAndGetId {
                it[SmartGateTable.eventId] = eventId
                it[SmartGateTable.name] = name
                it[SmartGateTable.uniqueRandId] = UUID.randomUUID().toString()
            }.value
            SmartGateTable
                .selectAll()
                .where(SmartGateTable.id eq id)
                .map { it[SmartGateTable.uniqueRandId] }
                .singleOrNull()
        }

    override suspend fun addSmartGateTier(
        eventId: Int,
        name: String,
        attendeeTier: AttendeeTier
    ): Int =
        transaction(database){
            val gateId = SmartGateTable
                .selectAll()
                .where(SmartGateTable.eventId eq eventId and(SmartGateTable.name eq name))
                .map { it[SmartGateTable.id].value }
                .singleOrNull() ?: return@transaction -1
            val tierId = EventTierTable
                .selectAll()
                .where({EventTierTable.id eq eventId and(EventTierTable.title eq attendeeTier.title)})
                .map { it[EventTierTable.id].value }
                .singleOrNull() ?: return@transaction -1
            TierGatePermissionTable
                .insertAndGetId {
                    it[TierGatePermissionTable.gateId] = gateId
                    it[TierGatePermissionTable.tierId] = tierId
                    it[TierGatePermissionTable.count] = 0
                }.value
        }

    override suspend fun removeSmartGateTier(
        eventId: Int,
        name: String,
        attendeeTier: AttendeeTier
    ): Int =
        transaction(database){
            val gateId = SmartGateTable
                .selectAll()
                .where(SmartGateTable.eventId eq eventId and(SmartGateTable.name eq name))
                .map { it[SmartGateTable.id].value }
                .singleOrNull() ?: return@transaction -1
            val tierId = EventTierTable
                .selectAll()
                .where({EventTierTable.id eq eventId and(EventTierTable.title eq attendeeTier.title)})
                .map { it[EventTierTable.id].value }
                .singleOrNull() ?: return@transaction -1
            TierGatePermissionTable
                .deleteWhere { TierGatePermissionTable.gateId eq gateId and(TierGatePermissionTable.tierId eq tierId) }
        }
}