package com.almaslowcore.oasis.features.activity.data.repository

import com.almaslowcore.oasis.features.activity.data.local.dao.ActivityDao
import com.almaslowcore.oasis.features.activity.data.local.entity.ActivityLogEntity
import com.almaslowcore.oasis.features.activity.data.local.entity.ActivitySubtaskLogEntity
import com.almaslowcore.oasis.features.activity.data.mapper.toDomainModel
import com.almaslowcore.oasis.features.activity.data.mapper.toEntity
import com.almaslowcore.oasis.features.activity.domain.model.ActivityDetailModel
import com.almaslowcore.oasis.features.activity.domain.model.ActivityModel
import com.almaslowcore.oasis.features.activity.domain.model.CreateActivityRequest
import com.almaslowcore.oasis.features.activity.domain.repository.ActivityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.UUID
import javax.inject.Inject

class ActivityRepositoryImpl @Inject constructor(
    private val activityDao: ActivityDao
) : ActivityRepository {

    override fun observeActivitiesForDate(
        date: String
    ): Flow<List<ActivityDetailModel>> {
        return combine(
            activityDao.observeActiveActivities(),
            activityDao.observeLogsByDate(date),
            activityDao.observeActiveSubtasks(),
            activityDao.observeSubtaskLogsByDate(date)
        ) { activities, logs, subtasks, subtaskLogs ->

            activities.map { activity ->
                val log = logs.firstOrNull {
                    it.activityId == activity.id
                }

                val activitySubtasks = subtasks
                    .filter { it.activityId == activity.id }
                    .map { subtask ->
                        val subtaskLog = subtaskLogs.firstOrNull {
                            it.subtaskId == subtask.id
                        }

                        subtask.toDomainModel(
                            isCompleted = subtaskLog?.isCompleted ?: false
                        )
                    }

                ActivityDetailModel(
                    activity = activity.toDomainModel(),
                    log = log?.toDomainModel(),
                    subtasks = activitySubtasks
                )
            }
        }
    }

    override fun observeActivityDetail(
        activityId: String,
        date: String
    ): Flow<ActivityDetailModel?> {
        return combine(
            activityDao.observeActivityById(activityId),
            activityDao.observeLogsForActivity(activityId),
            activityDao.observeSubtasksByActivityId(activityId),
            activityDao.observeSubtaskLogsByDate(date)
        ) { activity, logs, subtasks, subtaskLogs ->

            activity ?: return@combine null

            val todayLog = logs.firstOrNull {
                it.date == date
            }

            val subtaskModels = subtasks.map { subtask ->
                val subtaskLog = subtaskLogs.firstOrNull {
                    it.subtaskId == subtask.id
                }

                subtask.toDomainModel(
                    isCompleted = subtaskLog?.isCompleted ?: false
                )
            }

            ActivityDetailModel(
                activity = activity.toDomainModel(),
                log = todayLog?.toDomainModel(),
                subtasks = subtaskModels
            )
        }
    }

    override suspend fun createActivity(
        request: CreateActivityRequest
    ) {
        activityDao.upsertActivity(
            request.activity.toEntity()
        )

        if (request.subtasks.isNotEmpty()) {
            activityDao.upsertSubtasks(
                request.subtasks.map {
                    it.toEntity()
                }
            )
        }
    }

    override suspend fun updateActivity(
        activity: ActivityModel
    ) {
        activityDao.updateActivity(
            activity.toEntity(
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun archiveActivity(
        activityId: String
    ) {
        activityDao.archiveActivity(
            activityId = activityId,
            updatedAt = System.currentTimeMillis()
        )
    }

    override suspend fun updateActivityCompletion(
        activityId: String,
        date: String,
        isCompleted: Boolean,
        note: String?
    ) {
        val now = System.currentTimeMillis()

        val existingLog = activityDao.getLogByActivityIdAndDate(
            activityId = activityId,
            date = date
        )

        val normalizedNote = note?.trim()?.takeIf { it.isNotBlank() }

            if (existingLog == null) {
            activityDao.upsertActivityLog(
                ActivityLogEntity(
                    id = UUID.randomUUID().toString(),
                    activityId = activityId,
                    date = date,
                    isCompleted = isCompleted,
                    value = null,
                    note = normalizedNote,
                    createdAt = now,
                    updatedAt = now
                )
            )
        } else {
            activityDao.upsertActivityLog(
                existingLog.copy(
                    isCompleted = isCompleted,
                    note = normalizedNote ?: existingLog.note,
                    updatedAt = now
                )
            )
        }
    }

    override suspend fun updateNumericProgress(
        activityId: String,
        date: String,
        value: Double,
        note: String?
    ) {
        val now = System.currentTimeMillis()

        val existingLog = activityDao.getLogByActivityIdAndDate(
            activityId = activityId,
            date = date
        )

        val normalizedNote = note?.trim()?.takeIf { it.isNotBlank() }

        if (existingLog == null) {
            activityDao.upsertActivityLog(
                ActivityLogEntity(
                    id = UUID.randomUUID().toString(),
                    activityId = activityId,
                    date = date,
                    isCompleted = false,
                    value = value,
                    note = normalizedNote,
                    createdAt = now,
                    updatedAt = now
                )
            )
        } else {
            activityDao.upsertActivityLog(
                existingLog.copy(
                    value = value,
                    note = normalizedNote ?: existingLog.note,
                    updatedAt = now
                )
            )
        }
    }

    override suspend fun toggleSubtask(
        subtaskId: String,
        date: String,
        isCompleted: Boolean
    ) {
        val now = System.currentTimeMillis()

        val existingLog = activityDao.getSubtaskLogBySubtaskIdAndDate(
            subtaskId = subtaskId,
            date = date
        )

        val completedAt = if (isCompleted) now else null

        if (existingLog == null) {
            activityDao.upsertSubtaskLog(
                ActivitySubtaskLogEntity(
                    id = UUID.randomUUID().toString(),
                    subtaskId = subtaskId,
                    date = date,
                    isCompleted = isCompleted,
                    completedAt = completedAt,
                    updatedAt = now,
                    createdAt = now
                )
            )
        } else {
            activityDao.toggleSubtaskCompletion(
                subtaskId = subtaskId,
                date = date,
                isCompleted = isCompleted,
                completedAt = completedAt,
                updatedAt = now
            )
        }
    }
}