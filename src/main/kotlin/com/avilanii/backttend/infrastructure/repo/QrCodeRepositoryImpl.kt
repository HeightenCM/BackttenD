package com.avilanii.backttend.infrastructure.repo

import com.avilanii.backttend.domain.repo.QrCodeRepository
import com.avilanii.backttend.infrastructure.database.EventTable
import com.avilanii.backttend.infrastructure.database.ParticipantTable
import com.avilanii.backttend.infrastructure.database.QrCodeTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime
import java.util.UUID

class QrCodeRepositoryImpl(
    private val database: Database
): QrCodeRepository {
    override suspend fun registerExternalQrCode(userId: Int, qrCode: String): String =
        transaction(database) {
            val id = QrCodeTable.insertAndGetId {
                it[value] = qrCode
                it[QrCodeTable.userId] = userId
            }.value
            QrCodeTable.select(QrCodeTable.value).where(QrCodeTable.id eq id).map { it.toString() }.single()
        }

    override suspend fun getAllQrCodes(userId: Int): List<String> =
        transaction(database) {
            TODO("Implement the QR Wallet thing: will probably have to add a name for QRs.")
            QrCodeTable.selectAll().where(QrCodeTable.userId.eq(userId)).map {
                it.toString()
            }
        }

    override suspend fun getQrCode(eventId: Int, userId: Int): String =
        transaction(database) {
            ParticipantTable
                .select(ParticipantTable.qrCode)
                .where(ParticipantTable.userId eq userId)
                .andWhere { ParticipantTable.eventId eq eventId }
                .map { it[ParticipantTable.qrCode] }
                .single()
        }

    override suspend fun getTimeLimitedQrCode(eventId: Int, userId: Int): String =
        transaction(database) {
            val qrExpDate = EventTable.select(EventTable.qrexpirationdate)
                .where { EventTable.id eq eventId }
                .map { LocalDateTime.parse(it.toString()) }.single()
            if (qrExpDate.isAfter(LocalDateTime.now())) {
                EventTable.select(EventTable.qrcode).where { EventTable.id eq eventId }.map { it.toString() }.single()
            } else {
                EventTable.update {
                    it[EventTable.qrcode] = UUID.randomUUID().toString()
                    it[EventTable.qrexpirationdate] = LocalDateTime.now().plusMinutes(5).toString()
                }
                EventTable.select(EventTable.qrcode).where { EventTable.id eq eventId }.map { it.toString() }.single()
            }
        }
}