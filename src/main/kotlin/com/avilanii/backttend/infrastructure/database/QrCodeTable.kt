package com.avilanii.backttend.infrastructure.database

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object QrCodeTable : IntIdTable() {
    val userId = reference("user_id", UserTable.id, onDelete = ReferenceOption.CASCADE)
    val value = text("value")
    val title = varchar("title", 255)
}