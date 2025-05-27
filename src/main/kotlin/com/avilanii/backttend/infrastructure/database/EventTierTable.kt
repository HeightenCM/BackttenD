package com.avilanii.backttend.infrastructure.database

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object EventTierTable: IntIdTable("event_tiers") {
    val title = varchar("title", 255)
    val eventId = reference("event_id", EventTable, onDelete = ReferenceOption.CASCADE)
}