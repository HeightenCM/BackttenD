package com.avilanii.backttend.infrastructure.datatransferobjects

import com.avilanii.backttend.domain.models.QrCode

data class QrCodeResponseDTO(
    val data: List<QrCode>
)

fun List<QrCode>.toQrCodeResponseDTO(): QrCodeResponseDTO {
    return QrCodeResponseDTO(
        data = this)
}