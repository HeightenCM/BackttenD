package com.avilanii.backttend.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Event(
    val id: Int? = null,
    val name: String,
    val budget: Int,
    val dateTime: String,
    val isPending: Boolean? = null,
)
