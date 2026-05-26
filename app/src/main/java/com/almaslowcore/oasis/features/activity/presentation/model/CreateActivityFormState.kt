package com.almaslowcore.oasis.features.activity.presentation.model

import com.almaslowcore.oasis.features.activity.domain.model.ActivityTrackingType
import com.almaslowcore.oasis.features.activity.domain.model.ActivityType
import com.almaslowcore.oasis.features.activity.domain.model.MeasurableMode
import com.almaslowcore.oasis.features.activity.domain.model.RepeatEndType
import com.almaslowcore.oasis.features.activity.domain.model.RepeatUnit
import com.almaslowcore.oasis.features.activity.domain.model.TimeOfDay
import com.almaslowcore.oasis.features.planning.presentation.domain.model.LifeArea

data class CreateActivitySubtaskDraft(
    val id: String,
    val title: String,
    val orderIndex: Int
)

data class CreateActivityFormState(
    val iconName: String? = null,
    val colorHex: String? = null,

    val title: String = "",
    val description: String = "",

    val activityType: ActivityType = ActivityType.HABIT,
    val trackingType: ActivityTrackingType = ActivityTrackingType.YES_NO,
    val measurableMode: MeasurableMode? = null,

    val targetValueText: String = "",
    val unit: String = "",

    val subtasks: List<CreateActivitySubtaskDraft> = emptyList(),
    val subtaskInput: String = "",

    val categoryId: String? = null,
    val lifeAreaId: LifeArea? = null,

    val timeOfDay: TimeOfDay = TimeOfDay.ANYTIME,
    val specificTimeMinutes: Int? = null,

    val repeatEnabled: Boolean = false,
    val repeatIntervalText: String = "1",
    val repeatUnit: RepeatUnit? = RepeatUnit.DAY,
    val repeatStartDate: String = "",
    val repeatEndType: RepeatEndType = RepeatEndType.NEVER,
    val repeatEndDate: String = "",
    val repeatEndOccurrencesText: String = ""
) {
    val targetValue: Double?
        get() = targetValueText.toDoubleOrNull()

    val repeatInterval: Int?
        get() = repeatIntervalText.toIntOrNull()

    val repeatEndOccurrences: Int?
        get() = repeatEndOccurrencesText.toIntOrNull()


}