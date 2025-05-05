package com.avilanii.backttend.services

import com.avilanii.backttend.infrastructure.repo.QrCodeRepositoryImpl

class QrCodeService(
    private val qrCodeRepository: QrCodeRepositoryImpl
) {
    suspend fun registerExternalQrCode(userId: Int, qrCode: String): String{
        return qrCodeRepository.registerExternalQrCode(userId,qrCode)
    }
    suspend fun getAllQrCodes(participantId: Int): List<String>{
        return qrCodeRepository.getAllQrCodes(participantId)
    }
    suspend fun getTimeLimitedQrCode(eventId: Int, userId: Int): String{
        return qrCodeRepository.getTimeLimitedQrCode(eventId, userId)
    }
    suspend fun getQrCode(eventId: Int, userId: Int): String?{
        return qrCodeRepository.getQrCode(eventId, userId)
    }
}