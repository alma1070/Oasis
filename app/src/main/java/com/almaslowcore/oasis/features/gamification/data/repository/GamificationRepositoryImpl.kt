package com.almaslowcore.oasis.features.gamification.data.repository

import com.almaslowcore.oasis.features.gamification.data.local.dao.GamificationDao
import com.almaslowcore.oasis.features.gamification.data.local.entity.RewardEventEntity
import com.almaslowcore.oasis.features.gamification.data.local.entity.UserStatsEntity
import com.almaslowcore.oasis.features.gamification.data.mapper.calculateXpToNextLevel
import com.almaslowcore.oasis.features.gamification.data.mapper.toDomain
import com.almaslowcore.oasis.features.gamification.domain.model.RewardEvent
import com.almaslowcore.oasis.features.gamification.domain.model.RewardRequest
import com.almaslowcore.oasis.features.gamification.domain.model.UserStats
import com.almaslowcore.oasis.features.gamification.domain.repository.GamificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class GamificationRepositoryImpl @Inject constructor(
    private val dao: GamificationDao
) : GamificationRepository {

    override fun observeUserStats(): Flow<UserStats> {
        return dao.observeUserStats().map { entity ->
            entity?.toDomain() ?: UserStats(
                level = 1,
                currentXp = 0,
                xpToNextLevel = calculateXpToNextLevel(1),
                totalXp = 0,
                mindEnergy = 0,
                ecoEnergy = 0
            )
        }
    }

    override fun observeRecentRewards(limit: Int): Flow<List<RewardEvent>> {
        return dao.observeRecentRewards(limit).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun observeRewardsByDate(date: String): Flow<List<RewardEvent>> {
        return dao.observeRewardsByDate(date).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun ensureUserStatsExists() {
        val stats = dao.getUserStats()
        if (stats == null) {
            val now = System.currentTimeMillis()
            dao.upsertUserStats(
                UserStatsEntity(
                    id = "main",
                    level = 1,
                    currentXp = 0,
                    totalXp = 0,
                    mindEnergy = 0,
                    ecoEnergy = 0,
                    createdAt = now,
                    updatedAt = now
                )
            )
        }
    }

    override suspend fun awardReward(request: RewardRequest): Boolean {
        // Clamp reward amounts to 0 or more
        val xpToAward = request.xpAmount.coerceAtLeast(0)
        val mindToAward = request.mindEnergyAmount.coerceAtLeast(0)
        val ecoToAward = request.ecoEnergyAmount.coerceAtLeast(0)

        // Check whether reward event already exists
        val existing = dao.getRewardEvent(
            sourceType = request.sourceType.name,
            sourceId = request.sourceId,
            date = request.date
        )
        if (existing != null) return false

        val now = System.currentTimeMillis()
        
        // Prepare Reward Event Entity
        val rewardEvent = RewardEventEntity(
            id = UUID.randomUUID().toString(),
            sourceType = request.sourceType.name,
            sourceId = request.sourceId,
            date = request.date,
            xpAmount = xpToAward,
            mindEnergyAmount = mindToAward,
            ecoEnergyAmount = ecoToAward,
            reason = request.reason,
            createdAt = now
        )

        // Attempt to insert reward event
        val insertedId = dao.insertRewardEvent(rewardEvent)
        if (insertedId == -1L) return false

        // Update User Stats
        val currentStats = dao.getUserStats() ?: UserStatsEntity(
            id = "main",
            level = 1,
            currentXp = 0,
            totalXp = 0,
            mindEnergy = 0,
            ecoEnergy = 0,
            createdAt = now,
            updatedAt = now
        )

        var newLevel = currentStats.level
        var newCurrentXp = currentStats.currentXp + xpToAward
        val newTotalXp = currentStats.totalXp + xpToAward

        // Level up logic using while loop to handle multiple levels at once
        var xpToNext = calculateXpToNextLevel(newLevel)
        while (newCurrentXp >= xpToNext) {
            newCurrentXp -= xpToNext
            newLevel++
            xpToNext = calculateXpToNextLevel(newLevel)
        }

        val updatedStats = currentStats.copy(
            level = newLevel,
            currentXp = newCurrentXp,
            totalXp = newTotalXp,
            mindEnergy = currentStats.mindEnergy + mindToAward,
            ecoEnergy = currentStats.ecoEnergy + ecoToAward,
            updatedAt = now
        )

        dao.upsertUserStats(updatedStats)
        return true
    }
}
