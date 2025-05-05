package com.avilanii.backttend.infrastructure.database

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object QrCodeTable : IntIdTable("qrcode") {
    val value = text("value").uniqueIndex()
    val userId = reference("user_id", UserTable, onDelete = ReferenceOption.CASCADE)
}