package com.avilanii.backttend.domain.models

import java.time.LocalDateTime

data class Event(
    val id: Int,
    val name: String,
    val budget: Int,
    val dateTime: LocalDateTime
)
