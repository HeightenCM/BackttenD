package com.avilanii.backttend.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Participant(
    val eventId: Int,
    val userId: Int? = null,
    val name: String,
    val email: String,
    val status: ParticipantStatus = ParticipantStatus.PENDING,
    val role: ParticipantRole = ParticipantRole.ATTENDEE,
    val joinDate: String? = null
)

@Serializable
enum class ParticipantStatus{
    PENDING, ACCEPTED, REJECTED
}

fun ParticipantStatus.toBoolean(): Boolean? =
    when (this) {
        ParticipantStatus.PENDING -> true
        ParticipantStatus.ACCEPTED -> false
        ParticipantStatus.REJECTED -> false
    }

@Serializable
enum class ParticipantRole{
    ORGANIZER, ATTENDEE, HELPER
}