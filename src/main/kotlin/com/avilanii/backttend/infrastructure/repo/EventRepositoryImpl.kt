package com.avilanii.backttend.infrastructure.repo

import com.avilanii.backttend.domain.models.Event
import com.avilanii.backttend.domain.models.ParticipantRole
import com.avilanii.backttend.domain.models.ParticipantStatus
import com.avilanii.backttend.domain.repo.EventRepository
import com.avilanii.backttend.infrastructure.database.EventTable
import com.avilanii.backttend.infrastructure.database.ParticipantTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.util.UUID

class EventRepositoryImpl(
    private val database: Database,
): EventRepository {
    override suspend fun getAllEvents(userId: Int, participantRole: ParticipantRole): List<Event> =
        transaction (database) {EventTable.innerJoin(ParticipantTable)
                .select(EventTable.columns + ParticipantTable.status)
                .where { ParticipantTable.userId eq userId }
                .andWhere { ParticipantTable.role eq participantRole }
                .andWhere { ParticipantTable.status neq ParticipantStatus.REJECTED }
                .map {
                    Event(
                        id = it[EventTable.id].value,
                        name = it[EventTable.name],
                        venue = it[EventTable.venue],
                        startDateTime = it[EventTable.startDateTime],
                        endDateTime = it[EventTable.endDateTime],
                        isPending = if(participantRole == ParticipantRole.ATTENDEE){
                            it[ParticipantTable.status] == ParticipantStatus.PENDING
                        }
                            else
                                null
                        ,
                        organizer = ParticipantTable
                            .select(ParticipantTable.name)
                            .where{ ParticipantTable.eventId eq it[EventTable.id].value}
                            .andWhere { ParticipantTable.role eq ParticipantRole.ORGANIZER }
                            .map{ row -> row[ParticipantTable.name]}
                            .singleOrNull()
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
                        venue = result[EventTable.venue],
                        startDateTime = result[EventTable.startDateTime],
                        endDateTime = result[EventTable.endDateTime],
                    )
                }.singleOrNull()
        }

    override suspend fun addEvent(event: Event): Int =
        transaction (database){
            EventTable.insertAndGetId {
                it[EventTable.name] = event.name
                it[EventTable.venue] = event.venue
                it[EventTable.startDateTime] = event.startDateTime
                it[EventTable.endDateTime] = event.endDateTime
                it[EventTable.qrcode] = UUID.randomUUID().toString()
                it[EventTable.qrexpirationdate] = LocalDateTime.now().plusMinutes(5).toString()
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

    override suspend fun getEventByQr(qrCode: String): Event? =
        transaction(database){
            EventTable
                .selectAll()
                .where(EventTable.qrcode eq qrCode)
                .map { it ->
                    if (LocalDateTime.parse(it[EventTable.qrexpirationdate]!!).isAfter(LocalDateTime.now())) {
                        Event(
                            id = it[EventTable.id].value,
                            name = it[EventTable.name],
                            venue = it[EventTable.venue],
                            startDateTime = it[EventTable.startDateTime],
                            endDateTime = it[EventTable.endDateTime],
                            isPending = false,
                            organizer = ParticipantTable
                                .select(ParticipantTable.name)
                                .where { ParticipantTable.eventId eq it[EventTable.id].value }
                                .andWhere { ParticipantTable.role eq ParticipantRole.ORGANIZER }
                                .map { row -> row[ParticipantTable.name] }
                                .singleOrNull()
                        )
                    } else null
                }.singleOrNull()
        }
}