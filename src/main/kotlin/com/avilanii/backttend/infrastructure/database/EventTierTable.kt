package com.avilanii.backttend.infrastructure.database

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object EventTierTable: IntIdTable() {
    val title = varchar("title", 255)
    val eventId = reference("event_id", EventTable.id, onDelete = ReferenceOption.CASCADE)
    val organizerId = reference("user_id", UserTable.id, onDelete = ReferenceOption.CASCADE)
}