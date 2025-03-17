package com.avilanii.backttend.infrastructure.datatransferobjects

import kotlinx.serialization.Serializable

@Serializable
data class UserRegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

@Serializable
data class UserLoginRequest(
    val email: String,
    val password: String
)