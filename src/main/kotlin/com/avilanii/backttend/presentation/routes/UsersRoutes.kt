package com.avilanii.backttend.presentation.routes

import com.avilanii.backttend.services.UserService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.usersRoutes(
    userService: UserService
) {
    authenticate("auth-jwt") {
        route("/users") {
            delete("/{id}"){
                val id = call.parameters["id"]?.toIntOrNull() ?: return@delete call.respondText("Invalid id", status = HttpStatusCode.BadRequest)
                val requesterId = call.principal<JWTPrincipal>()?.payload?.subject!!.toInt()

            }
        }
    }
}