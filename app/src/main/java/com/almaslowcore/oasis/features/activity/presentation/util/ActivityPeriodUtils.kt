package com.almaslowcore.oasis.features.activity.presentation.util

import com.almaslowcore.oasis.features.activity.presentation.model.ActivityPeriodMode
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

data class ActivityDateRange(
    val startDate: LocalDate,
    val endDate: LocalDate
)

fun buildActivityDateRange(
    selectedDate: LocalDate,
    periodMode: ActivityPeriodMode
): ActivityDateRange {
    return when (periodMode) {
        ActivityPeriodMode.DAY -> ActivityDateRange(
            startDate = selectedDate,
            endDate = selectedDate
        )

        ActivityPeriodMode.WEEK -> {
            val startOfWeek = selectedDate.with(
                TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)
            )

            ActivityDateRange(
                startDate = startOfWeek,
                endDate = startOfWeek.plusDays(6)
            )
        }

        ActivityPeriodMode.MONTH -> ActivityDateRange(
            startDate = selectedDate.withDayOfMonth(1),
            endDate = selectedDate.withDayOfMonth(
                selectedDate.lengthOfMonth()
            )
        )
    }
}

fun LocalDate.toIsoDateString(): String {
    return toString()
}