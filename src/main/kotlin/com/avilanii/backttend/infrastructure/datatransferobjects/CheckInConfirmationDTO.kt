package com.avilanii.backttend.infrastructure.datatransferobjects

import kotlinx.serialization.Serializable

@Serializable
data class CheckInConfirmationDTO(
    val message: String,
    val accepted: Boolean,
)
