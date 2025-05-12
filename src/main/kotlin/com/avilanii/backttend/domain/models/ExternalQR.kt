package com.avilanii.backttend.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class ExternalQR(
    val value: String,
    val title: String
)
