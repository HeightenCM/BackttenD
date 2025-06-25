package com.avilanii.backttend.infrastructure.repo

import com.avilanii.backttend.domain.models.AttendeeTier
import com.avilanii.backttend.domain.models.Participant
import com.avilanii.backttend.domain.models.ParticipantInteraction
import com.avilanii.backttend.domain.models.ParticipantStatus
import com.avilanii.backttend.domain.repo.ParticipantRepository
import com.avilanii.backttend.infrastructure.database.EventTierTable
import com.avilanii.backttend.infrastructure.database.ParticipantInteractionTable
import com.avilanii.backttend.infrastructure.database.ParticipantTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime

class ParticipantRepositoryImpl(
    private val database: Database
): ParticipantRepository {
    override suspend fun getAllParticipants(eventId: Int): List<Participant> =
        transaction(database) {
            ParticipantTable.selectAll().where(ParticipantTable.eventId.eq(eventId)).map {
                Participant(
                    eventId = it[ParticipantTable.eventId].value,
                    userId = it[ParticipantTable.id].value,
                    name = it[ParticipantTable.name],
                    email = it[ParticipantTable.email],
                    status = it[ParticipantTable.status],
                    role = it[ParticipantTable.role],
                    qrCode = "",
                    tier = run {
                        val title = EventTierTable
                            .selectAll()
                            .where { EventTierTable.id eq (it[ParticipantTable.tierId]?.value) }
                            .map { it[EventTierTable.title] }
                            .singleOrNull()
                        AttendeeTier(
                            title = title ?: "No tier"
                        )
                    }
                )
            }
        }

    override suspend fun getParticipantByUserId(userId: Int): Participant? =
        transaction(database){
            ParticipantTable.selectAll().where(ParticipantTable.userId.eq(userId)).map { result->
                Participant(
                    eventId = result[ParticipantTable.eventId].value,
                    userId = result[ParticipantTable.userId]?.value,
                    name = result[ParticipantTable.name],
                    email = result[ParticipantTable.email],
                    status = result[ParticipantTable.status],
                    role = result[ParticipantTable.role],
                    qrCode = result[ParticipantTable.qrCode],
                )
            }.singleOrNull()
        }

    override suspend fun getParticipantById(id: Int): Participant? =
        transaction(database){
            ParticipantTable.selectAll().where(ParticipantTable.id.eq(id)).map { result ->
                Participant(
                    eventId = result[ParticipantTable.eventId].value,
                    userId = result[ParticipantTable.userId]?.value,
                    name = result[ParticipantTable.name],
                    email = result[ParticipantTable.email],
                    status = result[ParticipantTable.status],
                    role = result[ParticipantTable.role],
                    qrCode = result[ParticipantTable.qrCode],
                )
            }.singleOrNull()
        }

    override suspend fun addParticipant(participant: Participant): Int =
        transaction(database) {
            val participantId = ParticipantTable.insertAndGetId {
                it[eventId] = participant.eventId
                it[userId] = participant.userId
                it[name] = participant.name
                it[email] = participant.email
                it[status] = participant.status
                it[role] = participant.role
                it[qrCode] = participant.qrCode
            }.value
            ParticipantInteractionTable.insert {
                it[ParticipantInteractionTable.participantId] = participantId
                it[ParticipantInteractionTable.interaction] = ParticipantInteraction.JOIN
                it[ParticipantInteractionTable.date] = LocalDateTime.now().toString()
            }
            participantId
        }

    override suspend fun updateUserId(userId: Int, userEmail: String) {
        transaction(database) {
            ParticipantTable.update({ ParticipantTable.email eq userEmail }) {
                it[ParticipantTable.userId] = userId
            }
        }
    }

    override suspend fun updateParticipantStatus(userId: Int, eventId: Int, status: ParticipantStatus) =
        transaction(database) {
            val participantId = ParticipantTable
                .selectAll()
                .where{ ParticipantTable.userId eq userId and (ParticipantTable.eventId eq eventId)
                }.map { it[ParticipantTable.userId]!!.value }.single()
            ParticipantTable.update(
                where = {ParticipantTable.id eq participantId} ) {
                it[ParticipantTable.status] = status
            }
            val interaction = when(status) {
                ParticipantStatus.ACCEPTED -> {
                    ParticipantInteraction.JOIN
                }
                ParticipantStatus.CHECKED_IN -> {
                    ParticipantInteraction.CHECK_IN
                }
                ParticipantStatus.CHECKED_OUT -> {
                    ParticipantInteraction.CHECK_OUT
                }
                else -> null
            }
            if(interaction != null) {
                ParticipantInteractionTable.insert {
                    it[ParticipantInteractionTable.participantId] = participantId
                    it[ParticipantInteractionTable.interaction] = interaction
                    it[ParticipantInteractionTable.date] = LocalDateTime.now().toString()
                }
            }
            participantId
        }

    override suspend fun checkParticipationEnrollment(userId: Int, eventId: Int): Boolean =
        transaction(database){
            ParticipantTable
                .selectAll()
                .where{
                    (ParticipantTable.userId eq userId) and (ParticipantTable.eventId eq eventId)
                }
                .limit(1)
                .any()
        }

    override suspend fun checkParticipantEnrollmentByQr(eventId: Int, qrCode: String): Int? =
        transaction(database){
            ParticipantTable
                .selectAll()
                .where{
                    (ParticipantTable.eventId eq eventId) and (ParticipantTable.qrCode eq qrCode)
                }
                .map { it[ParticipantTable.id].value }
                .singleOrNull()
        }

    override suspend fun checkInCheckOutParticipant(participantId: Int, interaction: ParticipantInteraction): Boolean =
        transaction(database){
            val results = ParticipantInteractionTable
                .selectAll()
                .where { ParticipantInteractionTable.participantId eq participantId }
                .map {it[ParticipantInteractionTable.interaction] }
            var checkInCount = 0
            var checkOutCount = 0
            for(item in results) {
                if(item == ParticipantInteraction.CHECK_IN)
                    checkInCount++
                else if (item == ParticipantInteraction.CHECK_OUT)
                    checkOutCount++
            }
            if (interaction == ParticipantInteraction.CHECK_IN && checkInCount == checkOutCount) {
                ParticipantInteractionTable.insert {
                    it[ParticipantInteractionTable.participantId] = participantId
                    it[ParticipantInteractionTable.interaction] = ParticipantInteraction.CHECK_IN
                    it[ParticipantInteractionTable.date] = LocalDateTime.now().toString()
                }
                true
            } else if (interaction == ParticipantInteraction.CHECK_OUT && checkInCount > checkOutCount) {
                ParticipantInteractionTable.insert {
                    it[ParticipantInteractionTable.participantId] = participantId
                    it[ParticipantInteractionTable.interaction] = ParticipantInteraction.CHECK_OUT
                    it[ParticipantInteractionTable.date] = LocalDateTime.now().toString()
                }
                true
            } else false
        }

    override suspend fun addEventTier(organizerId: Int, eventId: Int, tier: String): Int =
        transaction(database){
            EventTierTable.insertAndGetId {
                it[EventTierTable.eventId] = eventId
                it[EventTierTable.title] = tier
                it[EventTierTable.organizerId] = organizerId
            }.value
        }

    override suspend fun deleteEventTier(organizerId: Int ,eventId: Int, tier: String): Int =
        transaction(database){
            EventTierTable.deleteWhere {
                EventTierTable.eventId eq eventId and (EventTierTable.title eq tier) and (EventTierTable.organizerId eq organizerId)
            }
        }

    override suspend fun getAllEventTiers(organizerId: Int, eventId: Int): List<AttendeeTier> =
        transaction(database){
            EventTierTable
                .selectAll()
                .where { EventTierTable.eventId eq eventId and (EventTierTable.organizerId eq organizerId) }
                .map { AttendeeTier(it[EventTierTable.title]) }
        }

    override suspend fun assignParticipantTier(
        organizerId: Int,
        participant: Participant,
        attendeeTier: AttendeeTier
    ) = transaction(database) {
            val tierId = EventTierTable
                .select(EventTierTable.id)
                .where{ EventTierTable.organizerId eq organizerId and (EventTierTable.title eq attendeeTier.title) }
                .map { it[EventTierTable.id].value }
                .singleOrNull()
            if (tierId != null) {
                ParticipantTable.update({ ParticipantTable.email eq participant.email and (ParticipantTable.name eq participant.name) }) {
                    it[ParticipantTable.tierId] = tierId
                }
                true
            } else false
        }

    override suspend fun removeParticipantTier(
        organizerId: Int,
        participant: Participant
    ) = transaction(database){
        ParticipantTable.update({ ParticipantTable.email eq participant.email and (ParticipantTable.name eq participant.name) }) {
            it[ParticipantTable.tierId] = null
        }
        }
}