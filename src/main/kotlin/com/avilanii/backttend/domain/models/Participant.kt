package com.avilanii.backttend.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Participant(
    val eventId: Int,
    val name: String,
    val email: String,
    val status: ParticipantStatus
)

@Serializable
enum class ParticipantStatus{
    PENDING, ACCEPTED, REJECTED
}

fun Int.toParticipantStatus(): ParticipantStatus{
    return when(this){
        0 -> ParticipantStatus.ACCEPTED
        1 -> ParticipantStatus.PENDING
        else -> ParticipantStatus.REJECTED
    }
}