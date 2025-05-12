package com.avilanii.backttend.services

import com.avilanii.backttend.domain.models.ExternalQR
import com.avilanii.backttend.infrastructure.repo.QrCodeRepositoryImpl

class QrCodeService(
    private val qrCodeRepository: QrCodeRepositoryImpl
) {
    suspend fun registerExternalQrCode(userId: Int, externalQR: ExternalQR): ExternalQR {
        return qrCodeRepository.registerExternalQrCode(userId,externalQR)
    }
    suspend fun getAllQrCodes(userId: Int): List<ExternalQR>{
        return qrCodeRepository.getAllQrCodes(userId)
    }
    suspend fun getTimeLimitedQrCode(eventId: Int, userId: Int): String{
        return qrCodeRepository.getTimeLimitedQrCode(eventId, userId)
    }
    suspend fun getQrCode(eventId: Int, userId: Int): String?{
        return qrCodeRepository.getQrCode(eventId, userId)
    }
}