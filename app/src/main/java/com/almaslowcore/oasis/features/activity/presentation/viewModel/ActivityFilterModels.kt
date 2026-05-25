package com.almaslowcore.oasis.features.activity.presentation.viewModel

import com.almaslowcore.oasis.features.activity.presentation.model.ActivityUiModel
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