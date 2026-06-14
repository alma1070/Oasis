package com.almaslowcore.oasis.features.gamification.domain.repository

import com.almaslowcore.oasis.features.gamification.domain.model.RewardEvent
import com.almaslowcore.oasis.features.gamification.domain.model.RewardRequest
import com.almaslowcore.oasis.features.gamification.domain.model.UserStats
import kotlinx.coroutines.flow.Flow

interface GamificationRepository {
    fun observeUserStats(): Flow<UserStats>

    fun observeRecentRewards(
        limit: Int = 10
    ): Flow<List<RewardEvent>>

    fun observeRewardsByDate(
        date: String
    ): Flow<List<RewardEvent>>

    suspend fun ensureUserStatsExists()

    suspend fun awardReward(
        request: RewardRequest
    ): Boolean
}
