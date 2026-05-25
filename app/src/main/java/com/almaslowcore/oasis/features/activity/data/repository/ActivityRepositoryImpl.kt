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
import com.almaslowcore.oasis.features.activity.domain.model.ActivityLogModel
import com.almaslowcore.oasis.features.activity.domain.model.ActivityPeriodDetailModel
import com.almaslowcore.oasis.features.activity.domain.model.ActivityPeriodSummaryModel
import com.almaslowcore.oasis.features.activity.domain.model.ActivitySubtaskLogModel
import com.almaslowcore.oasis.features.activity.domain.model.ActivitySubtaskModel
import java.time.LocalDate
import java.time.temporal.ChronoUnit
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

    override fun observeActivitiesForPeriod(
        startDate: String,
        endDate: String
    ): Flow<List<ActivityPeriodDetailModel>> {
        return combine(
            activityDao.observeActiveActivities(),
            activityDao.observeLogsBetweenDates(
                startDate = startDate,
                endDate = endDate
            ),
            activityDao.observeActiveSubtasks(),
            activityDao.observeSubtaskLogsBetweenDates(
                startDate = startDate,
                endDate = endDate
            )
        ) { activities, logs, subtasks, subtaskLogs ->

            val logsByActivityId = logs.groupBy { log ->
                log.activityId
            }

            val subtasksByActivityId = subtasks.groupBy { subtask ->
                subtask.activityId
            }

            activities.map { activity ->
                val activityLogs = logsByActivityId[activity.id]
                    .orEmpty()
                    .map { log ->
                        log.toDomainModel()
                    }

                val activitySubtasks = subtasksByActivityId[activity.id]
                    .orEmpty()

                val activitySubtaskIds = activitySubtasks
                    .map { subtask ->
                        subtask.id
                    }
                    .toSet()

                val activitySubtaskLogs = subtaskLogs
                    .filter { subtaskLog ->
                        subtaskLog.subtaskId in activitySubtaskIds
                    }
                    .map { subtaskLog ->
                        subtaskLog.toDomainModel()
                    }

                val subtaskLogsBySubtaskId = activitySubtaskLogs
                    .groupBy { subtaskLog ->
                        subtaskLog.subtaskId
                    }

                val subtaskModels = activitySubtasks.map { subtask ->
                    val latestSubtaskLog = subtaskLogsBySubtaskId[subtask.id]
                        .orEmpty()
                        .maxByOrNull { subtaskLog ->
                            subtaskLog.date
                        }

                    subtask.toDomainModel(
                        isCompleted = latestSubtaskLog?.isCompleted ?: false
                    )
                }

                val activityModel = activity.toDomainModel()

                ActivityPeriodDetailModel(
                    activity = activityModel,
                    logs = activityLogs,
                    subtasks = subtaskModels,
                    subtaskLogs = activitySubtaskLogs,
                    summary = buildPeriodSummary(
                        activity = activityModel,
                        logs = activityLogs,
                        subtasks = subtaskModels,
                        subtaskLogs = activitySubtaskLogs,
                        startDate = startDate,
                        endDate = endDate
                    )
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

private fun buildPeriodSummary(
    activity: ActivityModel,
    logs: List<ActivityLogModel>,
    subtasks: List<ActivitySubtaskModel>,
    subtaskLogs: List<ActivitySubtaskLogModel>,
    startDate: String,
    endDate: String
): ActivityPeriodSummaryModel {
    val periodDayCount = countDaysInclusive(
        startDate = startDate,
        endDate = endDate
    )

    val loggedDayCount = logs
        .map { log ->
            log.date
        }
        .distinct()
        .size

    val completedDayCount = logs.count { log ->
        log.isCompleted
    }

    val latestLog = logs.maxByOrNull { log ->
        log.date
    }

    val totalValue = logs
        .mapNotNull { log ->
            log.value
        }
        .takeIf { values ->
            values.isNotEmpty()
        }
        ?.sum()

    val targetValue = activity.targetValue?.let { dailyTarget ->
        if (dailyTarget > 0.0) {
            dailyTarget * periodDayCount
        } else {
            null
        }
    }

    val completedSubtaskLogCount = subtaskLogs.count { log ->
        log.isCompleted
    }

    val totalSubtaskPossibleCount = subtasks.size * periodDayCount

    val numericProgress = if (
        totalValue != null &&
        targetValue != null &&
        targetValue > 0.0
    ) {
        (totalValue / targetValue)
            .toFloat()
            .coerceIn(0f, 1f)
    } else {
        null
    }

    val checklistProgress = if (totalSubtaskPossibleCount > 0) {
        (completedSubtaskLogCount.toFloat() / totalSubtaskPossibleCount.toFloat())
            .coerceIn(0f, 1f)
    } else {
        null
    }

    val completionProgress = if (periodDayCount > 0) {
        (completedDayCount.toFloat() / periodDayCount.toFloat())
            .coerceIn(0f, 1f)
    } else {
        null
    }

    val progress = when {
        activity.measurableMode?.name == "NUMERIC" -> numericProgress
        activity.measurableMode?.name == "CHECKLIST" -> checklistProgress
        else -> completionProgress
    }

    val isCompleted = when {
        activity.measurableMode?.name == "NUMERIC" -> {
            progress != null && progress >= 1f
        }

        activity.measurableMode?.name == "CHECKLIST" -> {
            totalSubtaskPossibleCount > 0 &&
                    completedSubtaskLogCount >= totalSubtaskPossibleCount
        }

        else -> {
            periodDayCount > 0 &&
                    completedDayCount >= periodDayCount
        }
    }

    return ActivityPeriodSummaryModel(
        startDate = startDate,
        endDate = endDate,
        periodDayCount = periodDayCount,
        loggedDayCount = loggedDayCount,
        completedDayCount = completedDayCount,
        totalValue = totalValue,
        targetValue = targetValue,
        completedSubtaskLogCount = completedSubtaskLogCount,
        totalSubtaskPossibleCount = totalSubtaskPossibleCount,
        progress = progress,
        isCompleted = isCompleted,
        latestLog = latestLog
    )
}

private fun countDaysInclusive(
    startDate: String,
    endDate: String
): Int {
    val start = LocalDate.parse(startDate)
    val end = LocalDate.parse(endDate)

    return ChronoUnit.DAYS
        .between(start, end)
        .toInt() + 1
}