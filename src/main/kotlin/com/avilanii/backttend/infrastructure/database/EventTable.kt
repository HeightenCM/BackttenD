package com.avilanii.backttend.infrastructure.database

import org.jetbrains.exposed.dao.id.IntIdTable

object EventTable: IntIdTable() {
    val name = varchar("name", 255)
    val budget = integer("budget")
    val dateTime = text("dateTime")
    val qrcode = text("qrcode").nullable()
    val qrexpirationdate = text("qrexpirationdate").nullable()
}