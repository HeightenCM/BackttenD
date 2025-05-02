package com.avilanii.backttend.infrastructure.repo

import com.avilanii.backttend.domain.models.QrCode
import com.avilanii.backttend.domain.repo.QrCodeRepository
import com.avilanii.backttend.infrastructure.database.ParticipantTable
import com.avilanii.backttend.infrastructure.database.QrCodeTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class QrCodeRepositoryImpl(
    private val database: Database
): QrCodeRepository {
    override suspend fun registerQrCode(qrCode: QrCode) =
        transaction(database) {
            QrCodeTable.insertAndGetId {
                it[value] = qrCode.value
                it[eventId] = qrCode.eventId
                it[participantId] = qrCode.participantId
            }.value
        }

    override suspend fun getAllQrCodes(participantId: Int): List<QrCode> =
        transaction(database) {
            QrCodeTable.selectAll().where(QrCodeTable.participantId.eq(participantId)).map { result->
                QrCode(
                    value = result[QrCodeTable.value],
                    eventId = result[QrCodeTable.eventId].value,
                    participantId = result[QrCodeTable.participantId].value
                )
            }
        }

    override suspend fun getQrCode(eventId: Int, userId: Int): String? =
        transaction(database) {
            ParticipantTable
                .select(ParticipantTable.qrCode)
                .where(ParticipantTable.userId eq userId)
                .andWhere { ParticipantTable.eventId eq eventId }
                .map { it[ParticipantTable.qrCode] }
                .singleOrNull()
        }
}