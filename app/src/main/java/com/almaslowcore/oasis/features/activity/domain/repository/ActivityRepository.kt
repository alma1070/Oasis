package com.almaslowcore.oasis.features.activity.domain.repository

import com.almaslowcore.oasis.features.activity.domain.model.ActivityDetailModel
import com.almaslowcore.oasis.features.activity.domain.model.ActivityModel
import com.almaslowcore.oasis.features.activity.domain.model.CreateActivityRequest
import kotlinx.coroutines.flow.Flow

interface ActivityRepository {

    fun observeActivitiesForDate(
        date: String
    ): Flow<List<ActivityDetailModel>>

    fun observeActivityDetail(
        activityId: String,
        date: String
    ): Flow<ActivityDetailModel?>

    suspend fun createActivity(
        request: CreateActivityRequest
    )

    suspend fun updateActivity(
        activity: ActivityModel
    )

    suspend fun archiveActivity(
        activityId: String
    )

    suspend fun updateActivityCompletion(
        activityId: String,
        date: String,
        isCompleted: Boolean,
        note: String? = null
    )

    suspend fun updateNumericProgress(
        activityId: String,
        date: String,
        value: Double,
        note: String? = null
    )

    suspend fun toggleSubtask(
        subtaskId: String,
        date: String,
        isCompleted: Boolean
    )
}