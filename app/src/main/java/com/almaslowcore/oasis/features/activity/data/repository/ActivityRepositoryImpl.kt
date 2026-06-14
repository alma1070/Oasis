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
import com.almaslowcore.oasis.features.gamification.domain.model.RewardSourceType
import com.almaslowcore.oasis.features.gamification.domain.repository.GamificationRepository
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import com.almaslowcore.oasis.features.activity.data.local.entity.ActivityEntity
import com.almaslowcore.oasis.features.activity.data.local.entity.ActivitySubtaskEntity
import com.almaslowcore.oasis.features.activity.domain.model.ActivityTrackingType
import com.almaslowcore.oasis.features.activity.domain.model.ActivityType
import com.almaslowcore.oasis.features.activity.domain.model.MeasurableMode
import com.almaslowcore.oasis.features.activity.domain.model.RepeatUnit
import kotlinx.coroutines.flow.flatMapLatest

class ActivityRepositoryImpl @Inject constructor(
    private val activityDao: ActivityDao,
    private val gamificationRepository: GamificationRepository
) : ActivityRepository {

    override fun observeActivitiesForDate(date: String): Flow<List<ActivityPeriodDetailModel>> {
        val localDate = LocalDate.parse(date)

        return activityDao.observeAllActiveActivities().flatMapLatest { entities ->
            val filteredEntities = entities.filter { entity ->
                if (entity.activityType == ActivityType.TASK) {
                    entity.dueDate == date
                } else {
                    shouldActivityOccurOnDate(entity, localDate)
                }
            }

            if (filteredEntities.isEmpty()) return@flatMapLatest flowOf(emptyList())

            val activityIds = filteredEntities.map { it.id }
            combine(
                flowOf(filteredEntities),
                activityDao.observeLogsForActivitiesOnDate(activityIds, date),
                activityDao.observeSubtasksForActivities(activityIds),
                activityDao.observeSubtaskLogsForActivitiesOnDate(activityIds, date)
            ) { currentEntities, logs, subtasks, subtaskLogs ->
                currentEntities.map { entity ->
                    // FIX: Map to Domain models first to avoid type mismatch
                    val domainActivity = entity.toDomainModel()
                    val domainLogs = logs.filter { it.activityId == entity.id }.map { it.toDomainModel() }
                    
                    val activitySubtasks = subtasks.filter { it.activityId == entity.id }
                    val domainSubtasks = activitySubtasks.map { subtask ->
                        val isCompleted = subtaskLogs.any { it.subtaskId == subtask.id && it.isCompleted }
                        subtask.toDomainModel(isCompleted)
                    }

                    val domainSubtaskLogs = subtaskLogs.filter { log ->
                        activitySubtasks.any { it.id == log.subtaskId }
                    }.map { it.toDomainModel() }

                    ActivityPeriodDetailModel(
                        activity = domainActivity,
                        logs = domainLogs,
                        subtasks = domainSubtasks,
                        subtaskLogs = domainSubtaskLogs,
                        summary = buildPeriodSummary(
                            activity = domainActivity,
                            logs = domainLogs,
                            subtasks = domainSubtasks,
                            subtaskLogs = domainSubtaskLogs,
                            startDate = date, // For single date, start = end
                            endDate = date
                        )
                    )
                }
            }
        }
    }

    /**
     * Logic to determine if a Habit should show up on a specific date.
     */
    private fun shouldActivityOccurOnDate(activity: ActivityEntity, date: LocalDate): Boolean {
        val startDateStr = activity.repeatStartDate ?: return true
        val startDate = LocalDate.parse(startDateStr)

        // Habit hasn't started yet
        if (date.isBefore(startDate)) return false

        // Habit has ended
        activity.repeatEndDate?.let {
            if (date.isAfter(LocalDate.parse(it))) return false
        }

        val interval = activity.repeatInterval ?: 1
        return when (activity.repeatUnit) {
            RepeatUnit.DAY -> {
                ChronoUnit.DAYS.between(startDate, date) % interval == 0L
            }
            RepeatUnit.WEEK -> {
                val weeksBetween = ChronoUnit.WEEKS.between(startDate, date)
                weeksBetween % interval == 0L && date.dayOfWeek == startDate.dayOfWeek
            }
            RepeatUnit.MONTH -> {
                val monthsBetween = ChronoUnit.MONTHS.between(startDate, date)
                monthsBetween % interval == 0L && date.dayOfMonth == startDate.dayOfMonth
            }
            RepeatUnit.YEAR -> {
                val yearsBetween = ChronoUnit.YEARS.between(startDate, date)
                yearsBetween % interval == 0L &&
                        date.month == startDate.month &&
                        date.dayOfMonth == startDate.dayOfMonth
            }
            else -> true
        }
    }

    override fun observeActivitiesForPeriod(
        startDate: String,
        endDate: String
    ): Flow<List<ActivityPeriodDetailModel>> {
        val startLocalDate = LocalDate.parse(startDate)
        val endLocalDate = LocalDate.parse(endDate)

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
        ) { allActivities, logs, subtasks, subtaskLogs ->

            // FILTER: Only include activities that actually occur in this period
            val relevantActivities = allActivities.filter { entity ->
                if (entity.activityType == ActivityType.TASK) {
                    // Task must fall within the range
                    val due = entity.dueDate?.let { LocalDate.parse(it) }
                    due != null && !due.isBefore(startLocalDate) && !due.isAfter(endLocalDate)
                } else {
                    // Habit: Check if the habit's overall duration overlaps with the period
                    val habitStart = entity.repeatStartDate?.let { LocalDate.parse(it) } ?: startLocalDate
                    val habitEnd = entity.repeatEndDate?.let { LocalDate.parse(it) }

                    val startOverlap = !habitStart.isAfter(endLocalDate)
                    val endOverlap = habitEnd == null || !habitEnd.isBefore(startLocalDate)

                    startOverlap && endOverlap
                }
            }

            val logsByActivityId = logs.groupBy { it.activityId }
            val subtasksByActivityId = subtasks.groupBy { it.activityId }

            relevantActivities.map { activity ->
                // mapping logic
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
        val existingLog = activityDao.getLogByActivityIdAndDate(activityId, date)
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
        val existingLog = activityDao.getSubtaskLogBySubtaskIdAndDate(subtaskId, date)
        val completedAt = if (isCompleted) now else null

        if (existingLog == null) {
            activityDao.upsertSubtaskLog(
                ActivitySubtaskLogEntity(
                    id = UUID.randomUUID().toString(),
                    subtaskId = subtaskId,
                    date = date,
                    isCompleted = isCompleted,
                    completedAt = completedAt,
                    createdAt = now,
                    updatedAt = now
                )
            )
        } else {
            activityDao.upsertSubtaskLog(
                existingLog.copy(
                    isCompleted = isCompleted,
                    completedAt = completedAt,
                    updatedAt = now
                )
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
    val startLocalDate = LocalDate.parse(startDate)
    val endLocalDate = LocalDate.parse(endDate)
    val periodDayCount = (ChronoUnit.DAYS.between(startLocalDate, endLocalDate) + 1).toInt()

    // Progress calculation
    val progress: Float? = if (activity.trackingType == ActivityTrackingType.MEASURABLE) {
        if (activity.measurableMode == MeasurableMode.CHECKLIST) {
            if (subtasks.isEmpty()) 0f
            else (subtaskLogs.count { it.isCompleted }.toFloat() / subtasks.size.toFloat()).coerceIn(0f, 1f)
        } else {
            val target = activity.targetValue ?: 1.0
            val current = logs.firstOrNull()?.value ?: 0.0 // Simplified for daily/period
            (current / target).toFloat().coerceIn(0f, 1f)
        }
    } else {
        if (logs.any { it.isCompleted }) 1f else 0f
    }

    return ActivityPeriodSummaryModel(
        startDate = startDate,
        endDate = endDate,
        periodDayCount = periodDayCount,
        loggedDayCount = logs.size,
        completedDayCount = logs.count { it.isCompleted },
        totalValue = logs.sumOf { it.value ?: 0.0 },
        targetValue = activity.targetValue,
        completedSubtaskLogCount = subtaskLogs.count { it.isCompleted },
        totalSubtaskPossibleCount = subtasks.size,
        progress = progress,
        isCompleted = (progress ?: 0f) >= 1f,
        latestLog = logs.maxByOrNull { it.date }
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