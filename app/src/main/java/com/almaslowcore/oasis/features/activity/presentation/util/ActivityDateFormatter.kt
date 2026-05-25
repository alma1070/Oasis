package com.almaslowcore.oasis.features.activity.presentation.util

import com.almaslowcore.oasis.features.activity.presentation.model.ActivityFilterState
import com.almaslowcore.oasis.features.activity.presentation.model.ActivityPeriodMode
import com.almaslowcore.oasis.features.activity.presentation.model.ActivityTopBarUiState
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import java.util.Locale

// tìm cách chuyển ra util chung

fun buildActivityTopBarUiState(
    filterState: ActivityFilterState,
    today: LocalDate = LocalDate.now(),
    locale: Locale = Locale.getDefault()
): ActivityTopBarUiState {
    return buildActivityTopBarUiState(
        periodMode = filterState.periodMode,
        selectedDate = filterState.selectedDate,
        today = today,
        locale = locale
    )
}

fun buildActivityTopBarUiState(
    periodMode: ActivityPeriodMode,
    selectedDate: LocalDate,
    today: LocalDate = LocalDate.now(),
    locale: Locale = Locale.getDefault()
): ActivityTopBarUiState {
    return when (periodMode) {
        ActivityPeriodMode.DAY -> buildDayTopBarUiState(
            selectedDate = selectedDate,
            today = today,
            locale = locale
        )

        ActivityPeriodMode.WEEK -> buildWeekTopBarUiState(
            selectedDate = selectedDate,
            today = today,
            locale = locale
        )

        ActivityPeriodMode.MONTH -> buildMonthTopBarUiState(
            selectedDate = selectedDate,
            today = today,
            locale = locale
        )
    }
}

private fun buildDayTopBarUiState(
    selectedDate: LocalDate,
    today: LocalDate,
    locale: Locale
): ActivityTopBarUiState {
    return ActivityTopBarUiState(
        title = formatDayTitle(selectedDate, locale),
        subtitle = buildDaySubtitle(selectedDate, today, locale)
    )
}

private fun buildWeekTopBarUiState(
    selectedDate: LocalDate,
    today: LocalDate,
    locale: Locale
): ActivityTopBarUiState {
    val selectedWeekStart = getWeekStart(selectedDate)
    val selectedWeekEnd = getWeekEnd(selectedDate)
    val currentWeekStart = getWeekStart(today)

    val diff = ChronoUnit.WEEKS.between(currentWeekStart, selectedWeekStart)

    return ActivityTopBarUiState(
        title = "${formatDayMonth(selectedWeekStart, locale)} - ${formatDayMonth(selectedWeekEnd, locale)}",
        subtitle = buildWeekSubtitle(diff, locale)
    )
}

private fun buildMonthTopBarUiState(
    selectedDate: LocalDate,
    today: LocalDate,
    locale: Locale
): ActivityTopBarUiState {
    val selectedMonth = YearMonth.from(selectedDate)
    val currentMonth = YearMonth.from(today)

    val diff = ChronoUnit.MONTHS.between(currentMonth, selectedMonth)

    return ActivityTopBarUiState(
        title = formatMonthTitle(selectedDate, locale),
        subtitle = buildMonthSubtitle(diff, locale)
    )
}

private fun formatDayTitle(
    date: LocalDate,
    locale: Locale
): String {
    return if (locale.isVietnamese()) {
        "${formatVietnameseDayOfWeek(date.dayOfWeek)}, ${formatDayMonthVi(date)}"
    } else {
        date.format(DateTimeFormatter.ofPattern("EEEE, d MMMM", locale))
    }
}

private fun formatDayMonth(
    date: LocalDate,
    locale: Locale
): String {
    return if (locale.isVietnamese()) {
        formatDayMonthVi(date)
    } else {
        date.format(DateTimeFormatter.ofPattern("d MMM", locale))
    }
}

private fun formatMonthTitle(
    date: LocalDate,
    locale: Locale
): String {
    return if (locale.isVietnamese()) {
        "Tháng ${date.monthValue}"
    } else {
        date.format(DateTimeFormatter.ofPattern("MMMM", locale))
    }
}

private fun formatDayMonthVi(date: LocalDate): String {
    return "${date.dayOfMonth} Th${date.monthValue}"
}

private fun formatVietnameseDayOfWeek(dayOfWeek: DayOfWeek): String {
    return when (dayOfWeek) {
        DayOfWeek.MONDAY -> "Thứ 2"
        DayOfWeek.TUESDAY -> "Thứ 3"
        DayOfWeek.WEDNESDAY -> "Thứ 4"
        DayOfWeek.THURSDAY -> "Thứ 5"
        DayOfWeek.FRIDAY -> "Thứ 6"
        DayOfWeek.SATURDAY -> "Thứ 7"
        DayOfWeek.SUNDAY -> "Chủ nhật"
    }
}

private fun buildDaySubtitle(
    selectedDate: LocalDate,
    today: LocalDate,
    locale: Locale
): String {
    val diff = ChronoUnit.DAYS.between(today, selectedDate)

    return if (locale.isVietnamese()) {
        when {
            diff == 0L -> "Hôm nay"
            diff < 0L -> "${-diff} ngày trước"
            else -> "$diff ngày nữa"
        }
    } else {
        when {
            diff == 0L -> "Today"
            diff == -1L -> "1 day ago"
            diff < 0L -> "${-diff} days ago"
            diff == 1L -> "1 day later"
            else -> "$diff days later"
        }
    }
}

private fun buildWeekSubtitle(
    diff: Long,
    locale: Locale
): String {
    return if (locale.isVietnamese()) {
        when {
            diff == 0L -> "Tuần này"
            diff < 0L -> "${-diff} tuần trước"
            else -> "$diff tuần nữa"
        }
    } else {
        when {
            diff == 0L -> "This week"
            diff == -1L -> "1 week ago"
            diff < 0L -> "${-diff} weeks ago"
            diff == 1L -> "1 week later"
            else -> "$diff weeks later"
        }
    }
}

private fun buildMonthSubtitle(
    diff: Long,
    locale: Locale
): String {
    return if (locale.isVietnamese()) {
        when {
            diff == 0L -> "Tháng này"
            diff < 0L -> "${-diff} tháng trước"
            else -> "$diff tháng nữa"
        }
    } else {
        when {
            diff == 0L -> "This month"
            diff == -1L -> "1 month ago"
            diff < 0L -> "${-diff} months ago"
            diff == 1L -> "1 month later"
            else -> "$diff months later"
        }
    }
}

private fun getWeekStart(date: LocalDate): LocalDate {
    return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
}

private fun getWeekEnd(date: LocalDate): LocalDate {
    return getWeekStart(date).plusDays(6)
}

private fun Locale.isVietnamese(): Boolean {
    return language.equals("vi", ignoreCase = true)
}