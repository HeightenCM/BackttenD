package com.avilanii.backttend.infrastructure.database

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object TierGatePermissionTable: IntIdTable() {
    val gateId = reference("gate_id", SmartGateTable.id, onDelete = ReferenceOption.CASCADE)
    val tierId = reference("tier_id", EventTierTable.id, onDelete = ReferenceOption.CASCADE)
    val count = integer("count")
    val isAllowed = bool("isAllowed")
}