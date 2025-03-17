package com.avilanii.backttend.presentation.routes

import com.avilanii.backttend.infrastructure.datatransferobjects.UserLoginRequest
import com.avilanii.backttend.infrastructure.datatransferobjects.UserRegisterRequest
import com.avilanii.backttend.plugins.generateJWT
import com.avilanii.backttend.services.UserService
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.authRoutes(
    userService: UserService
) {
    route("/register") {
        post<UserRegisterRequest> { request: UserRegisterRequest ->
            if(userService.findByEmail(request.email)!=null)
                return@post call.respond(HttpStatusCode.Conflict, "User already exists")
            val createdUserId = userService.registerUser(request.name, request.email, request.password)?: return@post call.respond(HttpStatusCode.BadRequest)
            call.respond(HttpStatusCode.Created, createdUserId)
        }
    }
    route("/login") {
        post<UserLoginRequest> { request: UserLoginRequest ->
            val user = userService.login(request.email, request.password)?: return@post call.respond(HttpStatusCode.BadRequest)
            val token = generateJWT(user.id!!, user.password)
            call.respond(HttpStatusCode.OK, token)
        }
    }
}