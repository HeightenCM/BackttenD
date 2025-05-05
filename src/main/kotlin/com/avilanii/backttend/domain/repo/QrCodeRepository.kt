package com.avilanii.backttend.domain.repo

interface QrCodeRepository {
    suspend fun registerExternalQrCode(userId: Int, qrCode: String): String
    suspend fun getAllQrCodes(participantId: Int): List<String>
    suspend fun getQrCode(eventId: Int, userId: Int): String
    suspend fun getTimeLimitedQrCode(eventId: Int, userId: Int): String
}