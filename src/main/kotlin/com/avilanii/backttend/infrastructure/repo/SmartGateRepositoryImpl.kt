package com.avilanii.backttend.infrastructure.repo

import com.avilanii.backttend.domain.models.AttendeeTier
import com.avilanii.backttend.domain.models.ParticipantInteraction
import com.avilanii.backttend.domain.models.ParticipantStatus
import com.avilanii.backttend.domain.models.SmartGate
import com.avilanii.backttend.domain.repo.SmartGateRepository
import com.avilanii.backttend.infrastructure.database.EventTierTable
import com.avilanii.backttend.infrastructure.database.ParticipantInteractionTable
import com.avilanii.backttend.infrastructure.database.ParticipantTable
import com.avilanii.backttend.infrastructure.database.SmartGateTable
import com.avilanii.backttend.infrastructure.database.TierGatePermissionTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.plus
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.not
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime
import java.util.UUID
import kotlin.collections.map

class SmartGateRepositoryImpl(
    private val database: Database,
): SmartGateRepository {
    override suspend fun getSmartGates(eventId: Int): List<SmartGate> =
        transaction(database){
            SmartGateTable
                .selectAll()
                .where { SmartGateTable.eventId eq eventId }
                .map {
                    SmartGate(
                        id = it[SmartGateTable.id].value,
                        name = it[SmartGateTable.name],
                        isOnline = it[SmartGateTable.isOnline]
                    )
                }
        }

    override suspend fun addSmartGate(eventId: Int, name: String): SmartGate =
        transaction(database){
            val gateId = SmartGateTable.insertAndGetId {
                it[SmartGateTable.eventId] = eventId
                it[SmartGateTable.name] = name
                it[SmartGateTable.uniqueRandId] = UUID.randomUUID().toString()
                it[SmartGateTable.isOnline] = false
            }.value
            val tiers = EventTierTable
                .selectAll()
                .where { EventTierTable.eventId eq eventId }
                .map { AttendeeTier(
                    id = it[EventTierTable.id].value,
                    title = it[EventTierTable.title]
                ) }
            TierGatePermissionTable.batchInsert(tiers){ tier ->
                this[TierGatePermissionTable.gateId] = gateId
                this[TierGatePermissionTable.tierId] = tier.id!!
                this[TierGatePermissionTable.count] = 0
                this[TierGatePermissionTable.isAllowed] = false
            }
            SmartGate(
                id = gateId,
                name = name,
                isOnline = false
            )
        }

    override suspend fun changeGateTierState(gateId: Int, tierId: Int): Int =
        transaction(database) {
            TierGatePermissionTable.update({
                TierGatePermissionTable.gateId eq gateId and(TierGatePermissionTable.tierId eq tierId) }) {
                with(SqlExpressionBuilder) {
                    it[isAllowed] = not(isAllowed)
                }
            }
        }

    override suspend fun activateSmartGate(eventId: Int, gateId: Int): String =
        transaction(database) {
            SmartGateTable
                .selectAll()
                .where { SmartGateTable.id eq gateId and(SmartGateTable.eventId eq eventId) }
                .map { it[SmartGateTable.uniqueRandId] }
                .single()
        }

    override suspend fun getGateTiers(
        eventId: Int,
        gateId: Int
    ): List<AttendeeTier> =
        transaction(database) {
            val incompleteTiers = EventTierTable
                .selectAll()
                .where { EventTierTable.eventId eq eventId }
                .map { AttendeeTier(
                    title = it[EventTierTable.title],
                    id = it[EventTierTable.id].value,
                ) }
            val permissionsByTierId = TierGatePermissionTable
                .selectAll()
                .where { TierGatePermissionTable.gateId eq gateId }
                .associateBy(
                    keySelector = { it[TierGatePermissionTable.tierId].value },
                    valueTransform = {
                        Pair(
                            it[TierGatePermissionTable.isAllowed],
                            it[TierGatePermissionTable.count]
                        )
                    }
                )
            incompleteTiers.map { tier ->
                val (isAllowed, count) = permissionsByTierId[tier.id] ?: (false to 0)
                tier.copy(isAllowed = isAllowed, count = count)
            }
        }

    override suspend fun getGateId(gateIdentifier: String): Int? =
        transaction(database) {
            val gateId = SmartGateTable
                .selectAll()
                .where { SmartGateTable.uniqueRandId eq gateIdentifier }
                .map { it[SmartGateTable.id].value }
                .singleOrNull()
            SmartGateTable
                .update(
                    where = { SmartGateTable.id eq gateId },
                ){
                    it[SmartGateTable.isOnline] = true
                }
            gateId
        }

    override suspend fun getAttendeeByQr(attendeeIdentifier: String): Triple<Int, ParticipantStatus, Int?>? =
        transaction(database) {
            ParticipantTable
                .selectAll()
                .where { ParticipantTable.qrCode eq attendeeIdentifier }
                .map {
                    Triple(
                        it[ParticipantTable.id].value,
                        it[ParticipantTable.status],
                        it[ParticipantTable.tierId]?.value
                    )
                }
                .singleOrNull()
        }

    override suspend fun checkInAttendee(
        gateId: Int,
        attendeeId: Int,
        tierId: Int
    ): Boolean =
        transaction(database) {
            val isAllowed =
                TierGatePermissionTable
                    .selectAll()
                    .where { TierGatePermissionTable.gateId eq gateId and(TierGatePermissionTable.tierId eq tierId) }
                    .map { it[TierGatePermissionTable.isAllowed] }
                    .single()
            if (isAllowed) {
                ParticipantTable
                    .update({ ParticipantTable.id eq attendeeId }) {
                        it[ParticipantTable.status] = ParticipantStatus.CHECKED_IN
                    }
                ParticipantInteractionTable
                    .insert {
                        it[ParticipantInteractionTable.participantId] = attendeeId
                        it[ParticipantInteractionTable.date] = LocalDateTime.now().toString()
                        it[ParticipantInteractionTable.interaction] = ParticipantInteraction.CHECK_IN
                    }
                TierGatePermissionTable
                    .update(where =
                        { TierGatePermissionTable.gateId eq gateId and(TierGatePermissionTable.tierId eq tierId) }
                    ) {
                        with(TierGatePermissionTable) {
                            it[count] = count + 1
                        }
                    }
                true
            } else
                false
        }
}