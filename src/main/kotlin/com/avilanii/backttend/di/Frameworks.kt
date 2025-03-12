package com.avilanii.backttend.di

import com.avilanii.backttend.infrastructure.database.configureDatabase
import com.avilanii.backttend.infrastructure.repo.EventRepositoryImpl
import com.avilanii.backttend.services.EventService
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureFrameworks() {
    install(Koin) {
        slf4jLogger()
        modules(module {
            single {
                Database.connect("jdbc:sqlite:database.db", "org.sqlite.JDBC")
            }
            single {
                EventRepositoryImpl(get())
            }
            single {
                EventService(get())
            }
        })
    }
}
