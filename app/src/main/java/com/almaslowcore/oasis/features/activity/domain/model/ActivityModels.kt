package com.almaslowcore.oasis.features.activity.domain.model

enum class ActivityType {
    HABIT,
    TASK
}

enum class ActivityTrackingType {
    YES_NO,
    MEASURABLE
}

enum class MeasurableMode {
    NUMERIC,
    CHECKLIST
}

enum class TimeOfDay {
    START_OF_DAY,
    AFTERNOON,
    EVENING,
    BEDTIME,
    ANYTIME,
    SPECIFIC_TIME
}

enum class RepeatUnit {
    DAY,
    WEEK,
    MONTH,
    YEAR
}

enum class RepeatEndType {
    NEVER,
    ON_DATE,
    AFTER_OCCURRENCES
}

data class ActivityModel(
    val id: String,
    val title: String,
    val description: String? = null,
    val iconName: String? = null,
    val colorHex: String? = null,

    val activityType: ActivityType,
    val trackingType: ActivityTrackingType,
    val measurableMode: MeasurableMode? = null,

    val targetValue: Double? = null,
    val unit: String? = null,

    val categoryId: String? = null,
    val lifeAreaId: String? = null,

    val timeOfDay: TimeOfDay = TimeOfDay.ANYTIME,
    val specificTimeMinutes: Int? = null,

    val repeatEnabled: Boolean = false,
    val repeatInterval: Int? = null,
    val repeatUnit: RepeatUnit? = null,
    val repeatStartDate: String? = null,
    val repeatEndType: RepeatEndType = RepeatEndType.NEVER,
    val repeatEndDate: String? = null,
    val repeatEndOccurrences: Int? = null,

    val createdAt: Long,
    val updatedAt: Long,
    val isArchived: Boolean = false
)

data class ActivityLogModel(
    val id: String,
    val activityId: String,
    val date: String,
    val isCompleted: Boolean = false,
    val value: Double? = null,
    val note: String? = null,
    val createdAt: Long,
    val updatedAt: Long
)

data class ActivitySubtaskModel(
    val id: String,
    val activityId: String,
    val title: String,
    val orderIndex: Int,
    val isCompleted: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long,
    val isArchived: Boolean = false
)

data class ActivityDetailModel(
    val activity: ActivityModel,
    val log: ActivityLogModel?,
    val subtasks: List<ActivitySubtaskModel> = emptyList()
)

data class ActivitySubtaskLogModel(
    val id: String,
    val subtaskId: String,
    val date: String,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
    val createdAt: Long,
    val updatedAt: Long
)

data class CreateActivityRequest(
    val activity: ActivityModel,
    val subtasks: List<ActivitySubtaskModel> = emptyList()
)

data class ActivityPeriodSummaryModel(
    val startDate: String,
    val endDate: String,

    val periodDayCount: Int,

    val loggedDayCount: Int,
    val completedDayCount: Int,

    val totalValue: Double? = null,
    val targetValue: Double? = null,

    val completedSubtaskLogCount: Int = 0,
    val totalSubtaskPossibleCount: Int = 0,

    val progress: Float? = null,
    val isCompleted: Boolean = false,

    val latestLog: ActivityLogModel? = null
)

data class ActivityPeriodDetailModel(
    val activity: ActivityModel,
    val logs: List<ActivityLogModel> = emptyList(),
    val subtasks: List<ActivitySubtaskModel> = emptyList(),
    val subtaskLogs: List<ActivitySubtaskLogModel> = emptyList(),
    val summary: ActivityPeriodSummaryModel
)