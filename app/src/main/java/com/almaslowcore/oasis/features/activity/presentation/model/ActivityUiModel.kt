package com.almaslowcore.oasis.features.activity.presentation.model

enum class ActivityUiTrackingType {
    YES_NO,
    MEASURABLE
}

enum class ActivityUiMeasurableMode {
    NUMERIC,
    CHECKLIST
}

data class ActivitySubtaskUiModel(
    val id: String,
    val title: String,
    val isCompleted: Boolean = false,
    val orderIndex: Int
)

data class ActivityUiModel(
    val id: String,
    val title: String,
    val description: String? = null,

    val iconName: String? = null,
    val colorHex: String? = null,

    val isHabit: Boolean,

    val trackingType: ActivityUiTrackingType,
    val measurableMode: ActivityUiMeasurableMode? = null,

    val isCompleted: Boolean = false,

    val category: String? = null,
    val lifeArea: String? = null,

    val currentValue: Double? = null,
    val targetValue: Double? = null,
    val unit: String? = null,

    val streakCount: Int? = null,

    val dueText: String? = null,
    val repeatText: String? = null,

    val progress: Float? = null,

    val completedSubtaskCount: Int = 0,
    val totalSubtaskCount: Int = 0,
    val subtasks: List<ActivitySubtaskUiModel> = emptyList()
)