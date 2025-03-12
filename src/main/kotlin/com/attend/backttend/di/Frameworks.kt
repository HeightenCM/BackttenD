package com.attend.com.attend.backttend.di

import com.attend.com.attend.backttend.services.EventService
import io.ktor.server.application.*
import org.koin.core.module.dsl.singleOf
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
