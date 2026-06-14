package com.almaslowcore.oasis.features.activity.data.mapper

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.almaslowcore.oasis.R
import com.almaslowcore.oasis.features.activity.data.local.entity.ActivityEntity
import com.almaslowcore.oasis.features.activity.data.local.entity.ActivityLogEntity
import com.almaslowcore.oasis.features.activity.data.local.entity.ActivitySubtaskEntity
import com.almaslowcore.oasis.features.activity.data.local.entity.ActivitySubtaskLogEntity
import com.almaslowcore.oasis.features.activity.domain.model.ActivityTrackingType
import com.almaslowcore.oasis.features.activity.domain.model.ActivityType
import com.almaslowcore.oasis.features.activity.domain.model.MeasurableMode
import com.almaslowcore.oasis.features.activity.domain.model.RepeatEndType
import com.almaslowcore.oasis.features.activity.domain.model.RepeatUnit
import com.almaslowcore.oasis.features.activity.domain.model.TimeOfDay
import com.almaslowcore.oasis.features.activity.presentation.model.ActivitySubtaskUiModel
import com.almaslowcore.oasis.features.activity.presentation.model.ActivityUiMeasurableMode
import com.almaslowcore.oasis.features.activity.presentation.model.ActivityUiModel
import com.almaslowcore.oasis.features.activity.presentation.model.ActivityUiTrackingType
import java.util.Locale
import kotlin.math.roundToInt
/*
*
* Room Entity + Log
* ↓
* ActivityUiModel
* ↓
* ActivityCard / ActivityScreen
 */
@Composable
fun ActivityEntity.toUiModel(
    todayLog: ActivityLogEntity?,
    subtasks: List<ActivitySubtaskUiModel> = emptyList(),
    streakCount: Int? = null
): ActivityUiModel {
    val isHabit = activityType == ActivityType.HABIT

    val uiTrackingType = trackingType.toUiTrackingType()
    val uiMeasurableMode = measurableMode?.toUiMeasurableMode()

    val completedSubtaskCount = subtasks.count { it.isCompleted }
    val totalSubtaskCount = subtasks.size

    val checklistProgress = calculateChecklistProgress(
        completedCount = completedSubtaskCount,
        totalCount = totalSubtaskCount
    )

    val numericCurrentValue = todayLog?.value
    val numericProgress = calculateNumericProgress(
        currentValue = numericCurrentValue,
        targetValue = targetValue
    )

    val progress = when {
        trackingType == ActivityTrackingType.MEASURABLE &&
                measurableMode == MeasurableMode.CHECKLIST -> checklistProgress

        trackingType == ActivityTrackingType.MEASURABLE &&
                measurableMode == MeasurableMode.NUMERIC -> numericProgress

        else -> null
    }

    val isChecklistCompleted = totalSubtaskCount > 0 &&
            completedSubtaskCount == totalSubtaskCount

    val isCompleted = when {
        trackingType == ActivityTrackingType.MEASURABLE &&
                measurableMode == MeasurableMode.CHECKLIST -> {
            todayLog?.isCompleted ?: isChecklistCompleted
        }

        else -> {
            todayLog?.isCompleted ?: false
        }
    }

    return ActivityUiModel(
        id = id,
        title = title,
        description = description,

        iconName = iconName,
        colorHex = colorHex,

        isHabit = isHabit,
        trackingType = uiTrackingType,
        measurableMode = uiMeasurableMode,

        isCompleted = isCompleted,

        category = categoryId,
        lifeArea = lifeAreaId,

        categoryId = categoryId,
        categoryName = categoryId,
        lifeAreaId = lifeAreaId,
        lifeAreaName = lifeAreaId,

        timeOfDay = timeOfDay,
        specificTimeMinutes = specificTimeMinutes,

        currentValue = numericCurrentValue,
        targetValue = targetValue,
        unit = unit,

        streakCount = if (isHabit) streakCount else null,

        dueText = buildDueText(dueDate),
        repeatText = buildRepeatText(),

        progress = progress,

        completedSubtaskCount = completedSubtaskCount,
        totalSubtaskCount = totalSubtaskCount,
        subtasks = subtasks
    )
}

fun ActivitySubtaskEntity.toUiModel(
    todayLog: ActivitySubtaskLogEntity?
): ActivitySubtaskUiModel {
    return ActivitySubtaskUiModel(
        id = id,
        title = title,
        isCompleted = todayLog?.isCompleted ?: false,
        orderIndex = orderIndex
    )
}

fun List<ActivitySubtaskEntity>.toUiModels(
    logs: List<ActivitySubtaskLogEntity>
): List<ActivitySubtaskUiModel> {
    return map { subtask ->
        val log = logs.firstOrNull {
            it.subtaskId == subtask.id
        }

        subtask.toUiModel(
            todayLog = log
        )
    }.sortedBy {
        it.orderIndex
    }
}

private fun ActivityTrackingType.toUiTrackingType(): ActivityUiTrackingType {
    return when (this) {
        ActivityTrackingType.YES_NO -> ActivityUiTrackingType.YES_NO
        ActivityTrackingType.MEASURABLE -> ActivityUiTrackingType.MEASURABLE
    }
}

private fun MeasurableMode.toUiMeasurableMode(): ActivityUiMeasurableMode {
    return when (this) {
        MeasurableMode.NUMERIC -> ActivityUiMeasurableMode.NUMERIC
        MeasurableMode.CHECKLIST -> ActivityUiMeasurableMode.CHECKLIST
    }
}

private fun calculateNumericProgress(
    currentValue: Double?,
    targetValue: Double?
): Float? {
    if (targetValue == null || targetValue <= 0.0) return null

    val current = currentValue ?: 0.0

    return (current / targetValue)
        .toFloat()
        .coerceIn(0f, 1f)
}

private fun calculateChecklistProgress(
    completedCount: Int,
    totalCount: Int
): Float? {
    if (totalCount <= 0) return null

    return (completedCount.toFloat() / totalCount.toFloat())
        .coerceIn(0f, 1f)
}

@Composable
private fun ActivityEntity.buildRepeatText(): String? {
    if (activityType != ActivityType.HABIT) return null

    val interval = repeatInterval ?: 1
    val unit = repeatUnit ?: return null

    val repeatPart = when (unit) {
        RepeatUnit.DAY -> {
            if (interval == 1) stringResource(R.string.daily) else "${stringResource(R.string.every)} $interval ${stringResource(R.string.day)}"
        }

        RepeatUnit.WEEK -> {
            if (interval == 1) stringResource(R.string.weekly) else "${stringResource(R.string.every)} $interval ${stringResource(R.string.week)}"
        }

        RepeatUnit.MONTH -> {
            if (interval == 1) stringResource(R.string.monthly) else "${stringResource(R.string.every)} $interval ${stringResource(R.string.month)}"
        }

        RepeatUnit.YEAR -> {
            if (interval == 1) stringResource(R.string.yearly) else "${stringResource(R.string.every)} $interval ${stringResource(R.string.year)}"
        }
    }

    val endPart = when (repeatEndType) {
        RepeatEndType.NEVER -> null

        RepeatEndType.ON_DATE -> {
            repeatEndDate?.let {
                "${stringResource(R.string.to)} ${it.toDisplayDate()}"
            }
        }

        RepeatEndType.AFTER_OCCURRENCES -> {
            repeatEndOccurrences?.let {
                "${stringResource(R.string.after)} $it ${stringResource(R.string.occurrences)}"
            }
        }
    }

    return listOfNotNull(
        repeatPart,
        endPart
    ).joinToString(" · ")
}

@Composable
private fun ActivityEntity.buildDueText(dueDate: String?): String? {
    val timeText = when (timeOfDay) {
        TimeOfDay.START_OF_DAY -> stringResource(R.string.startOfDay)
        TimeOfDay.AFTERNOON -> stringResource(R.string.afternoon)
        TimeOfDay.EVENING -> stringResource(R.string.evening)
        TimeOfDay.BEDTIME -> stringResource(R.string.bedtime)
        TimeOfDay.ANYTIME -> stringResource(R.string.anytime)

        TimeOfDay.SPECIFIC_TIME -> {
            specificTimeMinutes?.toDisplayTime()
        }
    }
    return if (activityType == ActivityType.TASK && dueDate != null) {
        "${dueDate.toDisplayDate()} • $timeText"
    } else {
        timeText
    }
}

private fun Int.toDisplayTime(): String {
    val hour = this / 60
    val minute = this % 60

    return String.format(
        locale = Locale.getDefault(),
        format = "%02d:%02d",
        hour,
        minute
    )
}

private fun String.toDisplayDate(): String {
    val parts = split("-")

    if (parts.size != 3) return this

    val year = parts[0]
    val month = parts[1]
    val day = parts[2]

    return "$day/$month/$year"
}

fun Float.toPercentageText(): String {
    return "${(this * 100).roundToInt()}%"
}