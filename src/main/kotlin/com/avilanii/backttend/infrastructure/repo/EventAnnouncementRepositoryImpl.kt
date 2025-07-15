package com.avilanii.backttend.infrastructure.repo

import com.avilanii.backttend.domain.models.Announcement
import com.avilanii.backttend.domain.repo.AnnouncementRepository
import com.avilanii.backttend.infrastructure.database.EventAnnouncementTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class EventAnnouncementRepositoryImpl(
    private val database: Database
): AnnouncementRepository {
    override suspend fun getAnnouncements(eventId: Int): List<Announcement> =
        transaction(database) {
            EventAnnouncementTable
                .selectAll()
                .where { EventAnnouncementTable.eventId eq eventId }
                .map {
                    Announcement(
                        id = it[EventAnnouncementTable.id].value,
                        title = it[EventAnnouncementTable.title],
                        description = it[EventAnnouncementTable.description],
                        dateTime = it[EventAnnouncementTable.dateTime],
                    )
                }
        }

    override suspend fun postAnnouncement(
        eventId: Int,
        title: String,
        description: String
    ): Announcement =
        transaction(database) {
            val id = EventAnnouncementTable
                .insertAndGetId {
                    it[EventAnnouncementTable.eventId] = eventId
                    it[EventAnnouncementTable.title] = title
                    it[EventAnnouncementTable.description] = description
                    it[EventAnnouncementTable.dateTime] = LocalDateTime.now().toString()
                }
            Announcement(
                id = id.value,
                title = title,
                description = description,
                dateTime = LocalDateTime.now().toString()
            )
        }
}