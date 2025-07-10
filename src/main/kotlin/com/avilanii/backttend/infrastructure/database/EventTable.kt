package com.avilanii.backttend.infrastructure.database

import org.jetbrains.exposed.dao.id.IntIdTable

object EventTable: IntIdTable() {
    val name = varchar("name", 255)
    val venue = varchar("venue", 255)
    val startDateTime = text("startDateTime")
    val endDateTime = text("endDateTime")
    val qrcode = text("qrcode").nullable()
    val qrexpirationdate = text("qrexpirationdate").nullable()
}