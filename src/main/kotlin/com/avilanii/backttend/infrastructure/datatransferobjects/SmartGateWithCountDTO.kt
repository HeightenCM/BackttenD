package com.avilanii.backttend.infrastructure.datatransferobjects

import com.avilanii.backttend.domain.models.AttendeeTier
import kotlinx.serialization.Serializable

@Serializable
data class SmartGateWithCountDTO(
    val name: String,
    val allowedTiers: List<Pair<AttendeeTier, Int>>
)
