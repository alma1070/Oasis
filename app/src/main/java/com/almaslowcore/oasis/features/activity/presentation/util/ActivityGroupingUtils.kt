package com.almaslowcore.oasis.features.activity.presentation.util

import com.almaslowcore.oasis.features.activity.domain.model.TimeOfDay
import com.almaslowcore.oasis.features.activity.presentation.model.ActivityGroupBy
import com.almaslowcore.oasis.features.activity.presentation.model.ActivityListSectionUiModel
import com.almaslowcore.oasis.features.activity.presentation.model.ActivityUiModel

fun List<ActivityUiModel>.groupByMode(
    groupBy: ActivityGroupBy
): List<ActivityListSectionUiModel> {
    val sortedActivities = sortActivitiesForDisplay()

    return when (groupBy) {
        ActivityGroupBy.NONE -> listOf(
            ActivityListSectionUiModel(
                title = "",
                activities = sortedActivities
            )
        )

        ActivityGroupBy.TIME_OF_DAY -> groupByTimeOfDay()

        ActivityGroupBy.CATEGORY -> groupByCategory()

        ActivityGroupBy.LIFE_AREA -> groupByLifeArea()
    }
}

private fun List<ActivityUiModel>.groupByTimeOfDay(): List<ActivityListSectionUiModel> {
    return groupBy { activity ->
        activity.timeOfDay
    }
        .toSortedMap(
            compareBy { timeOfDay ->
                timeOfDay.sortOrder()
            }
        )
        .map { (timeOfDay, activities) ->
            ActivityListSectionUiModel(
                title = timeOfDay.toDisplayText(),
                activities = activities.sortActivitiesForDisplay()
            )
        }
}

private fun List<ActivityUiModel>.groupByCategory(): List<ActivityListSectionUiModel> {
    return groupBy { activity ->
        activity.categoryName
            ?.takeIf { it.isNotBlank() }
            ?: "Không có danh mục"
    }
        .toSortedMap()
        .map { (categoryName, activities) ->
            ActivityListSectionUiModel(
                title = categoryName,
                activities = activities.sortActivitiesForDisplay()
            )
        }
}

private fun List<ActivityUiModel>.groupByLifeArea(): List<ActivityListSectionUiModel> {
    return groupBy { activity ->
        activity.lifeAreaName
            ?.takeIf { it.isNotBlank() }
            ?: "Không có lĩnh vực"
    }
        .toSortedMap()
        .map { (lifeAreaName, activities) ->
            ActivityListSectionUiModel(
                title = lifeAreaName,
                activities = activities.sortActivitiesForDisplay()
            )
        }
}

private fun List<ActivityUiModel>.sortActivitiesForDisplay(): List<ActivityUiModel> {
    return sortedWith(
        compareBy<ActivityUiModel> { activity ->
            activity.specificTimeMinutes ?: Int.MAX_VALUE
        }
            .thenBy { activity ->
                activity.timeOfDay.sortOrder()
            }
            .thenBy { activity ->
                activity.title.lowercase()
            }
    )
}

private fun TimeOfDay.sortOrder(): Int {
    return when (this) {
        TimeOfDay.ANYTIME -> 0
        TimeOfDay.SPECIFIC_TIME -> 1
        TimeOfDay.START_OF_DAY -> 2
        TimeOfDay.AFTERNOON -> 3
        TimeOfDay.EVENING -> 4
        TimeOfDay.BEDTIME -> 5
    }
}

private fun TimeOfDay.toDisplayText(): String {
    return when (this) {
        TimeOfDay.ANYTIME -> "Anytime"
        TimeOfDay.SPECIFIC_TIME -> "Specific time"
        TimeOfDay.START_OF_DAY -> "Start of Day"
        TimeOfDay.AFTERNOON -> "Afternoon"
        TimeOfDay.EVENING -> "Evening"
        TimeOfDay.BEDTIME -> "Bedtime"
    }
}