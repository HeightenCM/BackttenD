package com.avilanii.backttend.infrastructure.database

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object QrCodeTable : IntIdTable("qrcode") {
    val userId = reference("user_id", UserTable, onDelete = ReferenceOption.CASCADE)
    val value = text("value").uniqueIndex()
    val title = varchar("title", 255)
}