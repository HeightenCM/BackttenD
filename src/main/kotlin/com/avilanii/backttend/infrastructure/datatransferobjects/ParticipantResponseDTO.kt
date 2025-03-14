package com.avilanii.backttend.infrastructure.datatransferobjects

import com.avilanii.backttend.domain.models.Participant
import kotlinx.serialization.Serializable

@Serializable
data class ParticipantResponseDTO(
    val data: List<Participant>
)

fun List<Participant>.toParticipantResponseDTO(): ParticipantResponseDTO {
    return ParticipantResponseDTO(this)
}