package com.almaslowcore.oasis.features.activity.presentation.util

import com.almaslowcore.oasis.features.activity.domain.model.TimeOfDay
import com.almaslowcore.oasis.features.activity.presentation.model.ActivityTimeOfDayFilter
import com.almaslowcore.oasis.features.activity.presentation.model.ActivityUiModel

fun List<ActivityUiModel>.filterByTimeOfDay(
    timeOfDayFilter: ActivityTimeOfDayFilter
): List<ActivityUiModel> {
    return when (timeOfDayFilter) {
        ActivityTimeOfDayFilter.ANYTIME -> this

        ActivityTimeOfDayFilter.START_OF_DAY -> filter {
            it.timeOfDay == TimeOfDay.START_OF_DAY
        }

        ActivityTimeOfDayFilter.AFTERNOON -> filter {
            it.timeOfDay == TimeOfDay.AFTERNOON
        }

        ActivityTimeOfDayFilter.EVENING -> filter {
            it.timeOfDay == TimeOfDay.EVENING
        }

        ActivityTimeOfDayFilter.BEDTIME -> filter {
            it.timeOfDay == TimeOfDay.BEDTIME
        }
    }
}

fun List<ActivityUiModel>.filterByCategories(
    selectedCategoryIds: Set<String>
): List<ActivityUiModel> {
    if (selectedCategoryIds.isEmpty()) {
        return this
    }

    return filter { activity ->
        activity.categoryId != null &&
                activity.categoryId in selectedCategoryIds
    }
}

fun List<ActivityUiModel>.filterByLifeAreas(
    selectedLifeAreaIds: Set<String>
): List<ActivityUiModel> {
    if (selectedLifeAreaIds.isEmpty()) {
        return this
    }

    return filter { activity ->
        activity.lifeAreaId != null &&
                activity.lifeAreaId in selectedLifeAreaIds
    }
}