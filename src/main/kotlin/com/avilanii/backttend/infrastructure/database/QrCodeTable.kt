package com.avilanii.backttend.infrastructure.database

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object QrCodeTable : IntIdTable("qrcode") {
    val value = long("value").uniqueIndex()
    val eventId = reference("event_id", EventTable, onDelete = ReferenceOption.CASCADE)
    val participantId = reference("participant_id", ParticipantTable, onDelete = ReferenceOption.CASCADE)
}