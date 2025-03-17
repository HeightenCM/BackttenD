package com.avilanii.backttend.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import java.util.*

fun Application.configureSecurity() {
    authentication {
        jwt ("auth-jwt") {
            realm = JWTConfig.jwtRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(JWTConfig.jwtSecret))
                    .withAudience(JWTConfig.jwtAudience)
                    .withIssuer(JWTConfig.jwtDomain)
                    .build()
            )
            validate { credential ->
                val userId = credential.payload.subject?.toIntOrNull()
                if (credential.payload.audience.contains(JWTConfig.jwtAudience) && userId != null) JWTPrincipal(credential.payload) else null
            }
            challenge { defaultScheme, realm ->
                call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
            }
        }
    }
}


private object JWTConfig{
    val jwtAudience = System.getenv("JWT_AUDIENCE") ?: "android"
    val jwtDomain = System.getenv("JWT_ISSUER") ?: "https://avilanii.ro/"
    val jwtRealm = System.getenv("JWT_REALM") ?: "backttend"
    val jwtSecret = System.getenv("JWT_SECRET") ?: "supermegagigasecretnotinenv"
}

fun generateJWT(userID: Int, email: String): String{
    return JWT.create()
        .withIssuer(JWTConfig.jwtDomain)
        .withAudience(JWTConfig.jwtAudience)
        .withSubject(userID.toString())
        .withClaim("email", email)
        //.withExpiresAt(Date(System.currentTimeMillis() + 3600000 * 24)) //maybe expire after 24h?
        .sign(Algorithm.HMAC256(JWTConfig.jwtSecret))
}