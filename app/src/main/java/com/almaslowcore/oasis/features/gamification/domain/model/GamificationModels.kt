package com.almaslowcore.oasis.features.gamification.domain.model

data class UserStats(
    val level: Int,
    val currentXp: Int,
    val xpToNextLevel: Int,
    val totalXp: Int,
    val mindEnergy: Int,
    val ecoEnergy: Int
) {
    val xpProgress: Float
        get() = if (xpToNextLevel <= 0) 0f else (currentXp.toFloat() / xpToNextLevel).coerceIn(0f, 1f)
}

enum class RewardSourceType {
    ACTIVITY,
    JOURNAL
}

data class RewardEvent(
    val id: String,
    val sourceType: RewardSourceType,
    val sourceId: String,
    val date: String?,
    val xpAmount: Int,
    val mindEnergyAmount: Int,
    val ecoEnergyAmount: Int,
    val reason: String,
    val createdAt: Long
)

data class RewardRequest(
    val sourceType: RewardSourceType,
    val sourceId: String,
    val date: String?,
    val xpAmount: Int,
    val mindEnergyAmount: Int = 0,
    val ecoEnergyAmount: Int = 0,
    val reason: String
)
