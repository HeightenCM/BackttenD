package com.avilanii.backttend.infrastructure.datatransferobjects

import com.avilanii.backttend.domain.models.Event
import kotlinx.serialization.Serializable

@Serializable
data class EventsResponseDTO(
    val data: List<Event>
)

fun List<Event>.toEventsResponseDTO(): EventsResponseDTO {
    return EventsResponseDTO(
        data = this
    )
}