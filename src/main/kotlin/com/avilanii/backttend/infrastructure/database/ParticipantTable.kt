package com.avilanii.backttend.infrastructure.database

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object ParticipantTable: Table("participants") {
    val name = text("name")
    val email = text("email")
    val status = integer("status")
    val event = reference("event_id", EventTable, onDelete = ReferenceOption.CASCADE)

    override val primaryKey = PrimaryKey(event)
}