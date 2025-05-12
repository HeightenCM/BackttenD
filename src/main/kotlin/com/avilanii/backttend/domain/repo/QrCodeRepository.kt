package com.avilanii.backttend.domain.repo

import com.avilanii.backttend.domain.models.ExternalQR

interface QrCodeRepository {
    suspend fun registerExternalQrCode(userId: Int, externalQR: ExternalQR): ExternalQR
    suspend fun getAllQrCodes(userId: Int): List<ExternalQR>
    suspend fun getQrCode(eventId: Int, userId: Int): String
    suspend fun getTimeLimitedQrCode(eventId: Int, userId: Int): String
}