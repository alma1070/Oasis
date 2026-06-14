package com.almaslowcore.oasis.features.gamification.data.mapper

import com.almaslowcore.oasis.features.gamification.data.local.entity.RewardEventEntity
import com.almaslowcore.oasis.features.gamification.data.local.entity.UserStatsEntity
import com.almaslowcore.oasis.features.gamification.domain.model.RewardEvent
import com.almaslowcore.oasis.features.gamification.domain.model.RewardSourceType
import com.almaslowcore.oasis.features.gamification.domain.model.UserStats

fun calculateXpToNextLevel(level: Int): Int {
    val safeLevel = if (level < 1) 1 else level
    return 100 + ((safeLevel - 1) * 50)
}

fun UserStatsEntity.toDomain(): UserStats {
    return UserStats(
        level = level,
        currentXp = currentXp,
        xpToNextLevel = calculateXpToNextLevel(level),
        totalXp = totalXp,
        mindEnergy = mindEnergy,
        ecoEnergy = ecoEnergy
    )
}

fun RewardEventEntity.toDomain(): RewardEvent {
    val mappedSourceType = runCatching {
        RewardSourceType.valueOf(sourceType)
    }.getOrDefault(RewardSourceType.ACTIVITY)

    return RewardEvent(
        id = id,
        sourceType = mappedSourceType,
        sourceId = sourceId,
        date = date,
        xpAmount = xpAmount,
        mindEnergyAmount = mindEnergyAmount,
        ecoEnergyAmount = ecoEnergyAmount,
        reason = reason,
        createdAt = createdAt
    )
}

fun RewardSourceType.toDataString(): String = this.name
