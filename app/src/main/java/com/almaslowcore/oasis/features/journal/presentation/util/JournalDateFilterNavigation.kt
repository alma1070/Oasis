package com.almaslowcore.oasis.features.journal.presentation.util

import com.almaslowcore.oasis.features.journal.presentation.state.JournalDateFilterState
import com.almaslowcore.oasis.features.journal.presentation.state.JournalFilterMode

fun JournalDateFilterState.previous(): JournalDateFilterState {
    return copy(
        selectedDate = when (filterMode) {
            JournalFilterMode.DAY -> selectedDate.minusDays(1)
            JournalFilterMode.WEEK -> selectedDate.minusWeeks(1)
            JournalFilterMode.MONTH -> selectedDate.minusMonths(1)
        }
    )
}

fun JournalDateFilterState.next(): JournalDateFilterState {
    return copy(
        selectedDate = when (filterMode) {
            JournalFilterMode.DAY -> selectedDate.plusDays(1)
            JournalFilterMode.WEEK -> selectedDate.plusWeeks(1)
            JournalFilterMode.MONTH -> selectedDate.plusMonths(1)
        }
    )
}