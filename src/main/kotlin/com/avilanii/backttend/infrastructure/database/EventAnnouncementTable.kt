package com.avilanii.backttend.infrastructure.database

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object EventAnnouncementTable: IntIdTable() {
    val eventId = reference("event_id", EventTable.id, ReferenceOption.CASCADE)
    val title = varchar("title", 255)
    val description = text("description")
    val dateTime = text("date_time")
}