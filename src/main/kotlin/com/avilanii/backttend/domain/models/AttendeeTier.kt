package com.avilanii.backttend.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class AttendeeTier(
    val id: Int? = null,
    val title: String,
    val count: Int? = null,
    val isAllowed: Boolean? = null
)
