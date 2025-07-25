package com.avilanii.backttend.infrastructure.database

import com.avilanii.backttend.domain.models.ParticipantRole
import com.avilanii.backttend.domain.models.ParticipantStatus
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object ParticipantTable: IntIdTable() {
    val eventId = reference("event_id", EventTable.id, onDelete = ReferenceOption.CASCADE)
    val userId = reference("user_id", UserTable.id, onDelete = ReferenceOption.SET_NULL).nullable()
    val name = varchar("name", 255)
    val email = varchar("email", 255)
    val status = enumerationByName("status", 50, ParticipantStatus::class)
    val role = enumerationByName("role", 50, ParticipantRole::class)
    val qrCode = text("qr_code")
    val tierId = reference("tier_id", EventTierTable.id, onDelete = ReferenceOption.SET_NULL).nullable()
}