package com.almaslowcore.oasis.features.gamification.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reward_events",
    indices = [
        Index(value = ["sourceType", "sourceId", "date"], unique = true)
    ]
)
data class RewardEventEntity(
    @PrimaryKey
    val id: String,
    val sourceType: String,
    val sourceId: String,
    val date: String?,
    val xpAmount: Int,
    val mindEnergyAmount: Int,
    val ecoEnergyAmount: Int,
    val reason: String,
    val createdAt: Long
)
