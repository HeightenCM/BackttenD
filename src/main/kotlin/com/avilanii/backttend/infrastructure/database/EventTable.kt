package com.avilanii.backttend.infrastructure.database

import org.jetbrains.exposed.dao.id.IntIdTable

object EventTable: IntIdTable("events") {
    val name = text("name")
    val budget = integer("budget")
    val dateTime = text("dateTime")
}