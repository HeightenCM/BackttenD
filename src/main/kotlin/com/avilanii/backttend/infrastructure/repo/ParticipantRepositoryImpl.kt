package com.avilanii.backttend.infrastructure.repo

import com.avilanii.backttend.domain.models.Participant
import com.avilanii.backttend.domain.models.toParticipantStatus
import com.avilanii.backttend.domain.repo.ParticipantRepository
import com.avilanii.backttend.infrastructure.database.ParticipantTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class ParticipantRepositoryImpl(
    private val database: Database
): ParticipantRepository {
    override suspend fun getAllParticipants(eventId: Int): List<Participant> =
        transaction(database) {
            ParticipantTable.selectAll().map {
                Participant(
                    eventId = eventId,
                    name = it[ParticipantTable.name],
                    email = it[ParticipantTable.email],
                    status = it[ParticipantTable.status].toParticipantStatus()
                )
            }
        }

    override suspend fun getParticipant(eventId: Int): Participant? =
        transaction(database){
            ParticipantTable.selectAll().where(ParticipantTable.event.eq(eventId)).map { result->
                Participant(
                    eventId = eventId,
                    name = result[ParticipantTable.name],
                    email = result[ParticipantTable.email],
                    status = result[ParticipantTable.status].toParticipantStatus()
                )
            }.singleOrNull()
        }

    override suspend fun addParticipant(participant: Participant): Unit =
        transaction(database) {
            ParticipantTable.insert {
                it[event] = participant.eventId
                it[name] = participant.name
                it[email] = participant.email
                it[status] = participant.status.ordinal
            }
        }
}