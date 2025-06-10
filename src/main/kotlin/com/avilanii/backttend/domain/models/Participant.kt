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
    val qrCode: String = "",
    val tier: AttendeeTier? = null,
)

@Serializable
enum class ParticipantStatus{
    PENDING, ACCEPTED, REJECTED, CHECKED_IN, CHECKED_OUT
}

@Serializable
enum class ParticipantRole{
    ORGANIZER, ATTENDEE, HELPER
}

@Serializable
enum class ParticipantInteraction{
    JOIN, CHECK_IN, CHECK_OUT
}