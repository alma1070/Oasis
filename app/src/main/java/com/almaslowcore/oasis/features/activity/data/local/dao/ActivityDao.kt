package com.almaslowcore.oasis.features.activity.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.almaslowcore.oasis.features.activity.data.local.entity.ActivityEntity
import com.almaslowcore.oasis.features.activity.data.local.entity.ActivityLogEntity
import com.almaslowcore.oasis.features.activity.data.local.entity.ActivitySubtaskEntity
import com.almaslowcore.oasis.features.activity.data.local.entity.ActivitySubtaskLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {

    // -------------------------------------------------------------------------
    // Activity queries
    // -------------------------------------------------------------------------

    @Upsert
    suspend fun upsertActivity(
        activity: ActivityEntity
    )

    @Update
    suspend fun updateActivity(
        activity: ActivityEntity
    )

    @Query(
        """
        UPDATE activities
        SET isArchived = 1,
            updatedAt = :updatedAt
        WHERE id = :activityId
        """
    )
    suspend fun archiveActivity(
        activityId: String,
        updatedAt: Long
    )

    @Query(
        """
        SELECT *
        FROM activities
        WHERE isArchived = 0
        ORDER BY createdAt DESC
        """
    )
    fun observeActiveActivities(): Flow<List<ActivityEntity>>

    @Query(
        """
        SELECT *
        FROM activities
        WHERE id = :activityId
        LIMIT 1
        """
    )
    suspend fun getActivityById(
        activityId: String
    ): ActivityEntity?

    @Query(
        """
        SELECT *
        FROM activities
        WHERE id = :activityId
        LIMIT 1
        """
    )
    fun observeActivityById(
        activityId: String
    ): Flow<ActivityEntity?>

    // -------------------------------------------------------------------------
    // Activity log queries
    // -------------------------------------------------------------------------

    @Upsert
    suspend fun upsertActivityLog(
        log: ActivityLogEntity
    )

    @Query(
        """
        SELECT *
        FROM activity_logs
        WHERE activityId = :activityId
          AND date = :date
        LIMIT 1
        """
    )
    suspend fun getLogByActivityIdAndDate(
        activityId: String,
        date: String
    ): ActivityLogEntity?

    @Query(
        """
        SELECT *
        FROM activity_logs
        WHERE date = :date
        """
    )
    fun observeLogsByDate(
        date: String
    ): Flow<List<ActivityLogEntity>>
    @Query(
        """
    SELECT *
    FROM activity_logs
    WHERE date BETWEEN :startDate AND :endDate
    ORDER BY activityId ASC, date ASC
    """
    )
    fun observeLogsBetweenDates(
        startDate: String,
        endDate: String
    ): Flow<List<ActivityLogEntity>>

    @Query(
        """
        SELECT *
        FROM activity_logs
        WHERE activityId = :activityId
        ORDER BY date DESC
        """
    )
    fun observeLogsForActivity(
        activityId: String
    ): Flow<List<ActivityLogEntity>>

    @Query(
        """
        UPDATE activity_logs
        SET isCompleted = :isCompleted,
            updatedAt = :updatedAt
        WHERE activityId = :activityId
          AND date = :date
        """
    )
    suspend fun updateActivityCompletionStatus(
        activityId: String,
        date: String,
        isCompleted: Boolean,
        updatedAt: Long
    )

    @Query(
        """
        UPDATE activity_logs
        SET value = :value,
            updatedAt = :updatedAt
        WHERE activityId = :activityId
          AND date = :date
        """
    )
    suspend fun updateActivityMeasurableValue(
        activityId: String,
        date: String,
        value: Double,
        updatedAt: Long
    )

    // -------------------------------------------------------------------------
    // Subtask queries
    // -------------------------------------------------------------------------

    @Upsert
    suspend fun upsertSubtasks(
        subtasks: List<ActivitySubtaskEntity>
    )

    @Upsert
    suspend fun upsertSubtask(
        subtask: ActivitySubtaskEntity
    )

    @Update
    suspend fun updateSubtask(
        subtask: ActivitySubtaskEntity
    )

    @Query(
        """
        UPDATE activity_subtasks
        SET isArchived = 1,
            updatedAt = :updatedAt
        WHERE id = :subtaskId
        """
    )
    suspend fun archiveSubtask(
        subtaskId: String,
        updatedAt: Long
    )

    @Query(
        """
        SELECT *
        FROM activity_subtasks
        WHERE activityId = :activityId
          AND isArchived = 0
        ORDER BY orderIndex ASC
        """
    )
    fun observeSubtasksByActivityId(
        activityId: String
    ): Flow<List<ActivitySubtaskEntity>>

    @Query(
        """
        UPDATE activity_subtasks
        SET orderIndex = :orderIndex,
            updatedAt = :updatedAt
        WHERE id = :subtaskId
        """
    )
    suspend fun updateSubtaskOrder(
        subtaskId: String,
        orderIndex: Int,
        updatedAt: Long
    )

    @Transaction
    suspend fun reorderSubtasks(
        orderedSubtaskIds: List<String>,
        updatedAt: Long
    ) {
        orderedSubtaskIds.forEachIndexed { index, subtaskId ->
            updateSubtaskOrder(
                subtaskId = subtaskId,
                orderIndex = index,
                updatedAt = updatedAt
            )
        }
    }
    @Query(
        """
    SELECT *
    FROM activity_subtasks
    WHERE isArchived = 0
    ORDER BY activityId ASC, orderIndex ASC
    """
    )
    fun observeActiveSubtasks(): Flow<List<ActivitySubtaskEntity>>

    // -------------------------------------------------------------------------
    // Subtask log queries
    // -------------------------------------------------------------------------

    @Upsert
    suspend fun upsertSubtaskLog(
        log: ActivitySubtaskLogEntity
    )

    @Query(
        """
        SELECT *
        FROM activity_subtask_logs
        WHERE subtaskId = :subtaskId
          AND date = :date
        LIMIT 1
        """
    )
    suspend fun getSubtaskLogBySubtaskIdAndDate(
        subtaskId: String,
        date: String
    ): ActivitySubtaskLogEntity?

    @Query(
        """
        SELECT *
        FROM activity_subtask_logs
        WHERE date = :date
        """
    )
    fun observeSubtaskLogsByDate(
        date: String
    ): Flow<List<ActivitySubtaskLogEntity>>
    @Query(
        """
    SELECT *
    FROM activity_subtask_logs
    WHERE date BETWEEN :startDate AND :endDate
    ORDER BY subtaskId ASC, date ASC
    """
    )
    fun observeSubtaskLogsBetweenDates(
        startDate: String,
        endDate: String
    ): Flow<List<ActivitySubtaskLogEntity>>
    @Query(
        """
        UPDATE activity_subtask_logs
        SET isCompleted = :isCompleted,
            completedAt = :completedAt,
            updatedAt = :updatedAt
        WHERE subtaskId = :subtaskId
          AND date = :date
        """
    )
    suspend fun toggleSubtaskCompletion(
        subtaskId: String,
        date: String,
        isCompleted: Boolean,
        completedAt: Long?,
        updatedAt: Long
    )

    // In ActivityDao.kt
    @Query("SELECT * FROM activities WHERE isArchived = 0")
    fun observeAllActiveActivities(): Flow<List<ActivityEntity>>

    @Query("SELECT * FROM activity_logs WHERE activityId IN (:activityIds) AND date = :date")
    fun observeLogsForActivitiesOnDate(activityIds: List<String>, date: String): Flow<List<ActivityLogEntity>>

    @Query("SELECT * FROM activity_subtasks WHERE activityId IN (:activityIds)")
    fun observeSubtasksForActivities(activityIds: List<String>): Flow<List<ActivitySubtaskEntity>>

    // In ActivityDao.kt

    @Query("""
    SELECT log.* FROM activity_subtask_logs AS log
    INNER JOIN activity_subtasks AS sub ON log.subtaskId = sub.id
    WHERE sub.activityId IN (:activityIds) AND log.date = :date
""")
    fun observeSubtaskLogsForActivitiesOnDate(
        activityIds: List<String>,
        date: String
    ): Flow<List<ActivitySubtaskLogEntity>>
}