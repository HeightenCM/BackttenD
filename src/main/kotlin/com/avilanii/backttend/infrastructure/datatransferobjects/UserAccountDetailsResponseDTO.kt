package com.avilanii.backttend.infrastructure.datatransferobjects

import kotlinx.serialization.Serializable

@Serializable
data class UserAccountDetailsResponseDTO(
    val name: String,
    val email: String,
)
