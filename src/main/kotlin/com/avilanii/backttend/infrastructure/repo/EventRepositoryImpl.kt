package com.avilanii.backttend.infrastructure.repo

import com.avilanii.backttend.domain.models.Event
import com.avilanii.backttend.domain.repo.EventRepository
import com.avilanii.backttend.infrastructure.database.EventTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class EventRepositoryImpl: EventRepository {
    override suspend fun getAllEvents(): List<Event> =
        transaction {
            EventTable.selectAll().map {
                Event(
                    id = it[EventTable.id].value,
                    name = it[EventTable.name],
                    budget = it[EventTable.budget],
                    dateTime = it[EventTable.dateTime]
                )
            }
        }

    override suspend fun getEvent(id: Int): Event? =
        transaction {
            EventTable.select(EventTable.id.eq(id)).map { result->
                Event(
                    id = result[EventTable.id].value,
                    name = result[EventTable.name],
                    budget = result[EventTable.budget],
                    dateTime = result[EventTable.dateTime]
                )
            }.singleOrNull()
        }

    override suspend fun addEvent(event: Event): Event? =
        transaction {
            val createdEventId = EventTable.insertAndGetId {
                it[EventTable.name] = event.name
                it[EventTable.budget] = event.budget
                it[EventTable.dateTime] =event.dateTime
            }.value
            EventTable.select(EventTable.id.eq(createdEventId)).map { result->
                Event(
                    id = result[EventTable.id].value,
                    name = result[EventTable.name],
                    budget = result[EventTable.budget],
                    dateTime = result[EventTable.dateTime]
                )
            }.singleOrNull()
        }
}