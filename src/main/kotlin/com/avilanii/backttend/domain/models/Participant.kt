package com.avilanii.backttend.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Participant(
    val eventId: Int,
    val userId: Int? = null,
    val name: String,
    val email: String,
    val status: ParticipantStatus,
    val role: ParticipantRole,
    val joinDate: String
)

@Serializable
enum class ParticipantStatus{
    PENDING, ACCEPTED, REJECTED
}

@Serializable
enum class ParticipantRole{
    ORGANIZER, ATTENDEE, HELPER
}

fun Int.toParticipantStatus(): ParticipantStatus{
    return when(this){
        0 -> ParticipantStatus.ACCEPTED
        1 -> ParticipantStatus.PENDING
        else -> ParticipantStatus.REJECTED
    }
}