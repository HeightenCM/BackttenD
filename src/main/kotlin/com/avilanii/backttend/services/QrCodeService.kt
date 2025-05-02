package com.avilanii.backttend.services

import com.avilanii.backttend.domain.models.QrCode
import com.avilanii.backttend.infrastructure.repo.QrCodeRepositoryImpl

class QrCodeService(
    private val qrCodeRepository: QrCodeRepositoryImpl
) {
    suspend fun registerQrCode(qrCode: QrCode): Int{
        return qrCodeRepository.registerQrCode(qrCode)
    }
    suspend fun getAllQrCodes(participantId: Int): List<QrCode>{
        return qrCodeRepository.getAllQrCodes(participantId)
    }

    suspend fun getQrCode(eventId: Int, userId: Int): String?{
        return qrCodeRepository.getQrCode(eventId, userId)
    }
}