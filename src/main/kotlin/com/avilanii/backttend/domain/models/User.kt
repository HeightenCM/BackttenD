package com.avilanii.backttend.domain.models

data class User(
    val id: Int? = null,
    val name: String,
    val email: String,
    val password: String
)
