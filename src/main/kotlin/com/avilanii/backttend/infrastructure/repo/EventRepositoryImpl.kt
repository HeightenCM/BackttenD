package com.avilanii.backttend.infrastructure.repo

import com.avilanii.backttend.domain.models.Event
import com.avilanii.backttend.domain.models.ParticipantRole
import com.avilanii.backttend.domain.models.toBoolean
import com.avilanii.backttend.domain.repo.EventRepository
import com.avilanii.backttend.infrastructure.database.EventTable
import com.avilanii.backttend.infrastructure.database.ParticipantTable
import com.avilanii.backttend.infrastructure.database.QrCodeTable.participantId
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class EventRepositoryImpl(
    private val database: Database,
): EventRepository {
    override suspend fun getAllEvents(userId: Int, participantRole: ParticipantRole): List<Event> =
        transaction (database) {
            EventTable.innerJoin(ParticipantTable)
                .select(EventTable.columns + ParticipantTable.status)
                .where { ParticipantTable.userId eq userId }
                .andWhere { ParticipantTable.role eq participantRole }
                .map {
                    Event(
                        id = it[EventTable.id].value,
                        name = it[EventTable.name],
                        budget = it[EventTable.budget],
                        dateTime = it[EventTable.dateTime],
                        isPending = if(participantRole == ParticipantRole.ATTENDEE)it[ParticipantTable.status].toBoolean()
                        else null,
                    )
                }
        }

    override suspend fun getEvent(eventId: Int): Event? =
        transaction (database) {
            EventTable
                .selectAll()
                .where(EventTable.id.eq(eventId))
                .map { result->
                    Event(
                        id = result[EventTable.id].value,
                        name = result[EventTable.name],
                        budget = result[EventTable.budget],
                        dateTime = result[EventTable.dateTime]
                    )
                }.singleOrNull()
        }

    override suspend fun addEvent(event: Event): Int =
        transaction (database){
            EventTable.insertAndGetId {
                it[EventTable.name] = event.name
                it[EventTable.budget] = event.budget
                it[EventTable.dateTime] =event.dateTime
            }.value
        }

    override suspend fun checkEventPermission(
        userId: Int,
        eventId: Int,
        requiredRole: List<ParticipantRole>
    ): Boolean =
        transaction(database) {
            ParticipantTable
                .select(ParticipantTable.role)
                .where {ParticipantTable.userId eq userId}
                .andWhere {ParticipantTable.eventId eq eventId}
                .map { it[ParticipantTable.role] }
                .any { it in requiredRole }
        }

    override suspend fun getEventRole(userId: Int, eventId: Int): ParticipantRole? =
        transaction(database){
            ParticipantTable
                .select(ParticipantTable.role)
                .where{ParticipantTable.userId eq userId}
                .andWhere { ParticipantTable.eventId eq eventId }
                .mapNotNull { it[ParticipantTable.role] }
                .singleOrNull()
        }
}