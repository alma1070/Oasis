package com.almaslowcore.oasis.features.journal.presentation.state

import com.almaslowcore.oasis.features.journal.domain.model.MoodType
import com.almaslowcore.oasis.features.journal.presentation.util.JournalDateRange
import java.time.LocalDate

data class JournalUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val filterMode: JournalFilterMode = JournalFilterMode.DAY,
    val dateRange: JournalDateRange? = null,

    val title: String = "",
    val subtitle: String = "",

    val daySections: List<JournalDaySectionUiState> = emptyList(),

    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

data class JournalDaySectionUiState(
    val date: LocalDate,
    val title: String,
    val entries: List<JournalEntryUiState>
)

data class JournalEntryUiState(
    val id: Long,
    val moodType: MoodType,
    val moodLabel: String,
    val moodEmoji: String,
    val timeText: String,
    val relatedActivityId: Long?,
    val relatedActivityTitle: String?,
    val note: String
)