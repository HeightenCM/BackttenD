package com.avilanii.backttend.infrastructure.repo

import com.avilanii.backttend.domain.models.Event
import com.avilanii.backttend.domain.models.ParticipantRole
import com.avilanii.backttend.domain.repo.EventRepository
import com.avilanii.backttend.infrastructure.database.EventTable
import com.avilanii.backttend.infrastructure.database.ParticipantTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class EventRepositoryImpl(
    private val database: Database,
): EventRepository {
    override suspend fun getAllEvents(userId: Int): List<Event> =
        transaction (database) {
            EventTable.innerJoin(ParticipantTable)
                .selectAll()
                .where(ParticipantTable.userId.eq(userId))
                .andWhere { ParticipantTable.role.eq(ParticipantRole.ORGANIZER) }
                .map {
                    Event(
                        id = it[EventTable.id].value,
                        name = it[EventTable.name],
                        budget = it[EventTable.budget],
                        dateTime = it[EventTable.dateTime]
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
}