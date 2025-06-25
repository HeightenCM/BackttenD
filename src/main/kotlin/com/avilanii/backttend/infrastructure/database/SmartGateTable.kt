package com.avilanii.backttend.infrastructure.database

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object SmartGateTable: IntIdTable() {
    val eventId = reference("event_id", EventTable.id, onDelete = ReferenceOption.CASCADE)
    val name = varchar("name", 255)
    val uniqueRandId = varchar("unique_id", 255)
}