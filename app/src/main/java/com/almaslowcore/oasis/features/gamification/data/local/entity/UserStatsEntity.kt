package com.almaslowcore.oasis.features.gamification.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_stats")
data class UserStatsEntity(
    @PrimaryKey
    val id: String = "main",
    val level: Int = 1,
    val currentXp: Int = 0,
    val totalXp: Int = 0,
    val mindEnergy: Int = 0,
    val ecoEnergy: Int = 0,
    val createdAt: Long,
    val updatedAt: Long
)
