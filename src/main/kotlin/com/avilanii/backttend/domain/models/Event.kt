package com.avilanii.backttend.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Event(
    val id: Int? = null,
    val name: String,
    val venue: String,
    val startDateTime: String,
    val endDateTime: String,
    val isPending: Boolean? = null,
    val organizer: String? = null,
)
