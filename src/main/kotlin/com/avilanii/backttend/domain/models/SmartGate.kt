package com.avilanii.backttend.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class SmartGate(
    val id: Int,
    val name: String,
    val isOnline: Boolean,
)
