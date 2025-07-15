package com.avilanii.backttend.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Announcement(
    val id: Int,
    val title: String,
    val description: String,
    val dateTime: String
)
