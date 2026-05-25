package com.almaslowcore.oasis.features.journal.presentation.util

import com.almaslowcore.oasis.features.journal.presentation.state.JournalDateFilterState
import com.almaslowcore.oasis.features.journal.presentation.state.JournalFilterMode
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId

data class JournalDateRange(
    val startDate: LocalDate,
    val endDateExclusive: LocalDate,
    val startTimeMillis: Long,
    val endTimeMillis: Long
)

fun JournalDateFilterState.toDateRange(
    zoneId: ZoneId = ZoneId.systemDefault(),
    firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY
): JournalDateRange {
    val startDate = when (filterMode) {
        JournalFilterMode.DAY -> selectedDate

        JournalFilterMode.WEEK -> selectedDate.startOfWeek(
            firstDayOfWeek = firstDayOfWeek
        )

        JournalFilterMode.MONTH -> selectedDate.withDayOfMonth(1)
    }

    val endDateExclusive = when (filterMode) {
        JournalFilterMode.DAY -> startDate.plusDays(1)
        JournalFilterMode.WEEK -> startDate.plusWeeks(1)
        JournalFilterMode.MONTH -> startDate.plusMonths(1)
    }

    return JournalDateRange(
        startDate = startDate,
        endDateExclusive = endDateExclusive,
        startTimeMillis = startDate.toStartOfDayMillis(zoneId),
        endTimeMillis = endDateExclusive.toStartOfDayMillis(zoneId)
    )
}

private fun LocalDate.startOfWeek(
    firstDayOfWeek: DayOfWeek
): LocalDate {
    val daysFromStartOfWeek =
        (dayOfWeek.value - firstDayOfWeek.value + 7) % 7

    return minusDays(daysFromStartOfWeek.toLong())
}

private fun LocalDate.toStartOfDayMillis(
    zoneId: ZoneId
): Long {
    return atStartOfDay(zoneId)
        .toInstant()
        .toEpochMilli()
}

