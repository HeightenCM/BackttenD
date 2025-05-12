package com.avilanii.backttend.infrastructure.repo

import com.avilanii.backttend.domain.models.ExternalQR
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
    override suspend fun registerExternalQrCode(userId: Int, externalQR: ExternalQR): ExternalQR =
        transaction(database) {
            val id = QrCodeTable.insertAndGetId {
                it[value] = externalQR.value
                it[title] = externalQR.title
                it[QrCodeTable.userId] = userId
            }.value
            QrCodeTable.select(columns = listOf(QrCodeTable.value, QrCodeTable.title))
                .where(QrCodeTable.id eq id)
                .map { resultRow ->
                    ExternalQR(
                        value = resultRow[QrCodeTable.value],
                        title = resultRow[QrCodeTable.title],
                    )
                }.single()
        }

    override suspend fun getAllQrCodes(userId: Int): List<ExternalQR> =
        transaction(database) {
            QrCodeTable.selectAll()
                .where(QrCodeTable.userId.eq(userId))
                .map { resultRow ->
                    ExternalQR(
                        value = resultRow[QrCodeTable.value],
                        title = resultRow[QrCodeTable.title],
                    )
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
                .map { row -> LocalDateTime.parse(row[EventTable.qrexpirationdate]!!) }.single()
            if (qrExpDate.isBefore(LocalDateTime.now())) {
                EventTable.update(
                    where = { EventTable.id eq eventId }
                ) {
                    it[EventTable.qrcode] = UUID.randomUUID().toString()
                    it[EventTable.qrexpirationdate] = LocalDateTime.now().plusMinutes(5).toString()
                }
            }
            EventTable.select(EventTable.qrcode).where { EventTable.id eq eventId }
                .map { row -> row[EventTable.qrcode]!! }.single()
        }
}