package com.avilanii.backttend.di

import com.avilanii.backttend.services.EventService
import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureFrameworks() {
    install(Koin) {
        slf4jLogger()
        modules(module {
            single<EventService> { EventService() }
        })
    }
}
