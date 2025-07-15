package com.avilanii.backttend.di

import com.avilanii.backttend.infrastructure.repo.EventAnnouncementRepositoryImpl
import com.avilanii.backttend.infrastructure.repo.EventRepositoryImpl
import com.avilanii.backttend.infrastructure.repo.ParticipantRepositoryImpl
import com.avilanii.backttend.infrastructure.repo.QrCodeRepositoryImpl
import com.avilanii.backttend.infrastructure.repo.SmartGateRepositoryImpl
import com.avilanii.backttend.infrastructure.repo.UserRepositoryImpl
import com.avilanii.backttend.services.AnnouncementService
import com.avilanii.backttend.services.EventService
import com.avilanii.backttend.services.ParticipantService
import com.avilanii.backttend.services.QrCodeService
import com.avilanii.backttend.services.SmartGateService
import com.avilanii.backttend.services.UserService
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
                Database.connect("jdbc:sqlite:src/main/resources/database.db", "org.sqlite.JDBC")
            }
            single {
                EventRepositoryImpl(get())
            }
            single {
                EventService(get())
            }
            single {
                ParticipantRepositoryImpl(get())
            }
            single {
                ParticipantService(get())
            }
            single {
                UserRepositoryImpl(get())
            }
            single {
                UserService(get())
            }
            single {
                QrCodeRepositoryImpl(get())
            }
            single {
                QrCodeService(get())
            }
            single {
                SmartGateRepositoryImpl(get())
            }
            single {
                SmartGateService(get())
            }
            single {
                EventAnnouncementRepositoryImpl(get())
            }
            single {
                AnnouncementService(get())
            }
        })
    }
}
