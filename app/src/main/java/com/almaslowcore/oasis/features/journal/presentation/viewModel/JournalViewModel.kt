package com.almaslowcore.oasis.features.journal.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almaslowcore.oasis.features.journal.domain.model.JournalEntry
import com.almaslowcore.oasis.features.journal.domain.repository.JournalRepository
import com.almaslowcore.oasis.features.journal.presentation.state.JournalDateFilterState
import com.almaslowcore.oasis.features.journal.presentation.state.JournalDaySectionUiState
import com.almaslowcore.oasis.features.activity.domain.repository.ActivityRepository
import com.almaslowcore.oasis.features.journal.presentation.state.JournalEntryUiState
import com.almaslowcore.oasis.features.journal.presentation.state.JournalFilterMode
import com.almaslowcore.oasis.features.journal.presentation.state.JournalUiState
import com.almaslowcore.oasis.features.journal.presentation.util.next
import com.almaslowcore.oasis.features.journal.presentation.util.previous
import com.almaslowcore.oasis.features.journal.presentation.util.toDateRange
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class JournalViewModel @Inject constructor(
    private val journalRepository: JournalRepository,
    private val activityRepository: ActivityRepository
) : ViewModel() {

    private val zoneId: ZoneId = ZoneId.systemDefault()

    private val dateFilterState = MutableStateFlow(
        JournalDateFilterState()
    )

    val uiState: StateFlow<JournalUiState> =
        dateFilterState
            .flatMapLatest { filterState ->
                val dateRange = filterState.toDateRange(zoneId = zoneId)

                combine(
                    journalRepository.observeEntriesBetween(
                        startTime = dateRange.startTimeMillis,
                        endTime = dateRange.endTimeMillis
                    ),
                    activityRepository.observeActivitiesForPeriod(
                        startDate = LocalDate.ofInstant(
                            Instant.ofEpochMilli(dateRange.startTimeMillis),
                            zoneId
                        ).toString(),
                        endDate = LocalDate.ofInstant(
                            Instant.ofEpochMilli(dateRange.endTimeMillis),
                            zoneId
                        ).toString()
                    )
                ) { entries, activities ->
                    val activityMap = activities.associateBy { it.activity.id }

                    JournalUiState(
                        selectedDate = filterState.selectedDate,
                        filterMode = filterState.filterMode,
                        dateRange = dateRange,
                        title = buildTitle(filterState),
                        subtitle = buildSubtitle(filterState),
                        daySections = entries.toDaySections(
                            filterMode = filterState.filterMode,
                            activityMap = activityMap
                        ),
                        isLoading = false,
                        errorMessage = null
                    )
                }
                .onStart {
                    emit(
                        JournalUiState(
                            selectedDate = filterState.selectedDate,
                            filterMode = filterState.filterMode,
                            dateRange = dateRange,
                            title = buildTitle(filterState),
                            subtitle = buildSubtitle(filterState),
                            isLoading = true
                        )
                    )
                }
                .catch { throwable ->
                    emit(
                        JournalUiState(
                            selectedDate = filterState.selectedDate,
                            filterMode = filterState.filterMode,
                            dateRange = dateRange,
                            title = buildTitle(filterState),
                            subtitle = buildSubtitle(filterState),
                            isLoading = false,
                            errorMessage = throwable.message ?: "Something went wrong"
                        )
                    )
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = JournalUiState(isLoading = true)
            )

    fun onPreviousDateRange() {
        dateFilterState.update {
            it.previous()
        }
    }

    fun onNextDateRange() {
        dateFilterState.update {
            it.next()
        }
    }

    fun onDateSelected(date: LocalDate) {
        dateFilterState.update {
            it.copy(selectedDate = date)
        }
    }

    fun onFilterModeSelected(mode: JournalFilterMode) {
        dateFilterState.update {
            it.copy(filterMode = mode)
        }
    }

    fun onDeleteEntry(entryId: Long) {
        viewModelScope.launch {
            journalRepository.deleteEntryById(entryId)
        }
    }

    private fun List<JournalEntry>.toDaySections(
        filterMode: JournalFilterMode,
        activityMap: Map<String, com.almaslowcore.oasis.features.activity.domain.model.ActivityPeriodDetailModel>
    ): List<JournalDaySectionUiState> {
        val groupedEntries = groupBy { entry ->
            entry.dateTime.toLocalDate()
        }

        return groupedEntries
            .toSortedMap(compareByDescending { it })
            .map { (date, entries) ->
                JournalDaySectionUiState(
                    date = date,
                    title = buildSectionTitle(date, filterMode),
                    entries = entries.map { entry ->
                        entry.toUiState(activityMap[entry.relatedActivityId]?.activity?.title)
                    }
                )
            }
    }

    private fun JournalEntry.toUiState(activityTitle: String? = null): JournalEntryUiState {
        return JournalEntryUiState(
            id = id,
            moodType = moodType,
            moodLabel = moodType.defaultLabel,
            moodEmoji = moodType.emoji,
            timeText = dateTime.toLocalTimeText(),
            relatedActivityId = relatedActivityId,
            relatedActivityTitle = activityTitle ?: relatedActivityTitle,
            note = note
        )
    }

    private fun Long.toLocalDate(): LocalDate {
        return Instant
            .ofEpochMilli(this)
            .atZone(zoneId)
            .toLocalDate()
    }

    private fun Long.toLocalTimeText(): String {
        val time = Instant
            .ofEpochMilli(this)
            .atZone(zoneId)
            .toLocalTime()

        return time.format(
            DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
        )
    }

    private fun buildSectionTitle(
        date: LocalDate,
        filterMode: JournalFilterMode
    ): String {
        return when (filterMode) {
            JournalFilterMode.DAY -> "Mood check-ins"
            JournalFilterMode.WEEK,
            JournalFilterMode.MONTH -> {
                date.format(
                    DateTimeFormatter.ofPattern("EEEE, d MMM", Locale.getDefault())
                )
            }
        }
    }

    private fun buildTitle(
        filterState: JournalDateFilterState
    ): String {
        val selectedDate = filterState.selectedDate

        return when (filterState.filterMode) {
            JournalFilterMode.DAY -> {
                selectedDate.format(
                    DateTimeFormatter.ofPattern("EEEE, d MMM", Locale.getDefault())
                )
            }

            JournalFilterMode.WEEK -> {
                val range = filterState.toDateRange(zoneId = zoneId)

                val startText = range.startDate.format(
                    DateTimeFormatter.ofPattern("d MMM", Locale.getDefault())
                )

                val endText = range.endDateExclusive
                    .minusDays(1)
                    .format(
                        DateTimeFormatter.ofPattern("d MMM", Locale.getDefault())
                    )

                "$startText - $endText"
            }

            JournalFilterMode.MONTH -> {
                selectedDate.format(
                    DateTimeFormatter.ofPattern("MMMM", Locale.getDefault())
                )
            }
        }
    }

    private fun buildSubtitle(
        filterState: JournalDateFilterState
    ): String {
        val today = LocalDate.now(zoneId)

        return when (filterState.filterMode) {
            JournalFilterMode.DAY -> {
                val diff = ChronoUnit.DAYS.between(today, filterState.selectedDate)

                when {
                    diff == 0L -> "Today"
                    diff < 0L -> "${kotlin.math.abs(diff)} days ago"
                    else -> "$diff days later"
                }
            }

            JournalFilterMode.WEEK -> {
                val selectedRange = filterState.toDateRange(zoneId = zoneId)
                val currentRange = JournalDateFilterState(
                    selectedDate = today,
                    filterMode = JournalFilterMode.WEEK
                ).toDateRange(zoneId = zoneId)

                val diff = ChronoUnit.WEEKS.between(
                    currentRange.startDate,
                    selectedRange.startDate
                )

                when {
                    diff == 0L -> "This week"
                    diff < 0L -> "${kotlin.math.abs(diff)} weeks ago"
                    else -> "$diff weeks later"
                }
            }

            JournalFilterMode.MONTH -> {
                val selectedMonth = filterState.selectedDate.withDayOfMonth(1)
                val currentMonth = today.withDayOfMonth(1)

                val diff = ChronoUnit.MONTHS.between(
                    currentMonth,
                    selectedMonth
                )

                when {
                    diff == 0L -> "This month"
                    diff < 0L -> "${kotlin.math.abs(diff)} months ago"
                    else -> "$diff months later"
                }
            }
        }
    }
}