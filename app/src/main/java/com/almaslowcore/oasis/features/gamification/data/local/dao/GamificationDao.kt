package com.almaslowcore.oasis.features.gamification.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.almaslowcore.oasis.features.gamification.data.local.entity.RewardEventEntity
import com.almaslowcore.oasis.features.gamification.data.local.entity.UserStatsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GamificationDao {

    @Query("SELECT * FROM user_stats WHERE id = 'main' LIMIT 1")
    fun observeUserStats(): Flow<UserStatsEntity?>

    @Query("SELECT * FROM user_stats WHERE id = 'main' LIMIT 1")
    suspend fun getUserStats(): UserStatsEntity?

    @Upsert
    suspend fun upsertUserStats(userStats: UserStatsEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRewardEvent(rewardEvent: RewardEventEntity): Long

    @Query("""
        SELECT * FROM reward_events 
        WHERE sourceType = :sourceType 
        AND sourceId = :sourceId 
        AND ((date IS NULL AND :date IS NULL) OR (date = :date)) 
        LIMIT 1
    """)
    suspend fun getRewardEvent(sourceType: String, sourceId: String, date: String?): RewardEventEntity?

    @Query("SELECT * FROM reward_events ORDER BY createdAt DESC LIMIT :limit")
    fun observeRecentRewards(limit: Int): Flow<List<RewardEventEntity>>

    @Query("SELECT * FROM reward_events WHERE date = :date ORDER BY createdAt DESC")
    fun observeRewardsByDate(date: String): Flow<List<RewardEventEntity>>
}
