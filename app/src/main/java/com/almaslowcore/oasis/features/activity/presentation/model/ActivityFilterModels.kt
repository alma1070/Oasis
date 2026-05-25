package com.almaslowcore.oasis.features.activity.presentation.model

import java.time.LocalDate

enum class ActivityPeriodMode {
    DAY,
    WEEK,
    MONTH
}

enum class ActivityTimeOfDayFilter {
    ANYTIME,
    START_OF_DAY,
    AFTERNOON,
    EVENING,
    BEDTIME
}

enum class ActivityGroupBy {
    NONE,
    TIME_OF_DAY,
    CATEGORY,
    LIFE_AREA
}

data class ActivityFilterState(
    val periodMode: ActivityPeriodMode = ActivityPeriodMode.DAY,
    val selectedDate: LocalDate = LocalDate.now(),
    val timeOfDayFilter: ActivityTimeOfDayFilter = ActivityTimeOfDayFilter.ANYTIME,
    val selectedCategoryIds: Set<String> = emptySet(),
    val selectedLifeAreaIds: Set<String> = emptySet(),
    val groupBy: ActivityGroupBy = ActivityGroupBy.NONE
)

data class ActivityTopBarUiState(
    val title: String = "",
    val subtitle: String = ""
)

data class ActivityListSectionUiModel(
    val title: String,
    val activities: List<ActivityUiModel>
)