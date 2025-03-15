package com.avilanii.backttend.domain.repo

import com.avilanii.backttend.domain.models.QrCode

interface QrCodeRepository {
    suspend fun registerQrCode(qrCode: QrCode): Int
    suspend fun getAllQrCodes(participantId: Int): List<QrCode>
}