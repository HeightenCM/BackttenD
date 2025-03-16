package com.avilanii.backttend.infrastructure.repo

import com.avilanii.backttend.domain.models.Participant
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
                    userId = it[ParticipantTable.id].value,
                    name = it[ParticipantTable.name],
                    email = it[ParticipantTable.email],
                    status = it[ParticipantTable.status],
                    role = it[ParticipantTable.role],
                    joinDate = it[ParticipantTable.joinDate],
                )
            }
        }

    override suspend fun getParticipant(id: Int): Participant? =
        transaction(database){
            ParticipantTable.selectAll().where(ParticipantTable.eventId.eq(id)).map { result->
                Participant(
                    eventId = id,
                    userId = result[ParticipantTable.userId]?.value,
                    name = result[ParticipantTable.name],
                    email = result[ParticipantTable.email],
                    status = result[ParticipantTable.status],
                    role = result[ParticipantTable.role],
                    joinDate = result[ParticipantTable.joinDate],
                )
            }.singleOrNull()
        }

    override suspend fun addParticipant(participant: Participant): Int =
        transaction(database) {
            ParticipantTable.insertAndGetId {
                it[eventId] = participant.eventId
                it[userId] = participant.userId
                it[name] = participant.name
                it[email] = participant.email
                it[status] = participant.status
                it[role] = participant.role
                it[joinDate] = participant.joinDate
            }.value
        }
}