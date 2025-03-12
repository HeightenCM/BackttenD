package com.avilanii.backttend.infrastructure.database

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database

fun Application.configureDatabase() {
    Database.connect("jdbc:sqlite:/data/data.db", "org.sqlite.JDBC")
}