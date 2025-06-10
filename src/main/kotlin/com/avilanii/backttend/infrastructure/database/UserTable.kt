package com.avilanii.backttend.infrastructure.database

import org.jetbrains.exposed.dao.id.IntIdTable

object UserTable : IntIdTable() {
    val name = varchar("name", 255)
    val email = varchar("email", 255).uniqueIndex()
    val passwordHash = varchar("passwordhash", 255)
}