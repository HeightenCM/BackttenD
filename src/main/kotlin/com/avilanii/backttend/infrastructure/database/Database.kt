package com.avilanii.backttend.infrastructure.database

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.koin.ktor.ext.inject

fun Application.configureDatabase() {
    println("Configuring database...")
    val db by inject<Database>()
    db.apply {
        transaction {
            create(EventTable)
            create(ParticipantTable)
        }
    }
}