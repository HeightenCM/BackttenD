package com.avilanii.backttend.infrastructure.database

import com.avilanii.backttend.domain.models.ParticipantInteraction
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object ParticipantInteractionTable : IntIdTable() {
    val participantId = reference("participant_id", ParticipantTable.id, onDelete = ReferenceOption.CASCADE)
    val interaction = enumeration("interaction", ParticipantInteraction::class)
    val date = text("date")
}