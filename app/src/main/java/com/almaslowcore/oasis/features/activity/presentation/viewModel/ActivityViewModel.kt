package com.almaslowcore.oasis.features.activity.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almaslowcore.oasis.features.activity.domain.model.ActivityDetailModel
import com.almaslowcore.oasis.features.activity.domain.model.ActivityModel
import com.almaslowcore.oasis.features.activity.domain.model.ActivitySubtaskModel
import com.almaslowcore.oasis.features.activity.domain.model.CreateActivityRequest
import com.almaslowcore.oasis.features.activity.domain.repository.ActivityRepository
import com.almaslowcore.oasis.features.activity.presentation.model.ActivityFilterState
import com.almaslowcore.oasis.features.activity.presentation.model.ActivityGroupBy
import com.almaslowcore.oasis.features.activity.presentation.model.ActivityListSectionUiModel
import com.almaslowcore.oasis.features.activity.presentation.model.ActivityPeriodMode
import com.almaslowcore.oasis.features.activity.presentation.model.ActivityScreenUiState
import com.almaslowcore.oasis.features.activity.presentation.model.ActivitySubtaskUiModel
import com.almaslowcore.oasis.features.activity.presentation.model.ActivityTimeOfDayFilter
import com.almaslowcore.oasis.features.activity.presentation.model.ActivityUiMeasurableMode
import com.almaslowcore.oasis.features.activity.presentation.model.ActivityUiModel
import com.almaslowcore.oasis.features.activity.presentation.model.ActivityUiTrackingType
import com.almaslowcore.oasis.features.activity.presentation.model.CreateActivityFormState
import com.almaslowcore.oasis.features.activity.presentation.model.CreateActivityUiState
import com.almaslowcore.oasis.features.activity.presentation.util.buildActivityTopBarUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject
import com.almaslowcore.oasis.features.activity.presentation.util.filterByCategories
import com.almaslowcore.oasis.features.activity.presentation.util.filterByLifeAreas
import com.almaslowcore.oasis.features.activity.presentation.util.filterByTimeOfDay
import com.almaslowcore.oasis.features.activity.presentation.util.groupByMode
import com.almaslowcore.oasis.features.activity.presentation.util.buildActivityDateRange
import com.almaslowcore.oasis.features.activity.presentation.util.toIsoDateString
import com.almaslowcore.oasis.features.activity.domain.model.ActivityPeriodDetailModel
@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val repository: ActivityRepository
) : ViewModel() {
    private val filterState = MutableStateFlow(
        ActivityFilterState()
    )

    private val selectedActivityId = MutableStateFlow<String?>(null)

    private val isFilterSheetOpen = MutableStateFlow(false)
    private val isDatePickerOpen = MutableStateFlow(false)
    private val isGroupByMenuOpen = MutableStateFlow(false)
    @OptIn(ExperimentalCoroutinesApi::class)
    private val activityDetailsFlow =
        filterState
            .map { filter ->
                buildActivityDateRange(
                    selectedDate = filter.selectedDate,
                    periodMode = filter.periodMode
                )
            }
            .distinctUntilChanged()
            .flatMapLatest { range ->
                repository.observeActivitiesForPeriod(
                    startDate = range.startDate.toIsoDateString(),
                    endDate = range.endDate.toIsoDateString()
                )
            }
    private val createActivityUiState = MutableStateFlow(
        CreateActivityUiState()
    )

    val createState: StateFlow<CreateActivityUiState> = createActivityUiState

    private val screenControlState: StateFlow<ActivityScreenControlState> =
        combine(
            selectedActivityId,
            isFilterSheetOpen,
            isDatePickerOpen,
            isGroupByMenuOpen
        ) { selectedActivityId, isFilterSheetOpen, isDatePickerOpen, isGroupByMenuOpen ->
            ActivityScreenControlState(
                selectedActivityId = selectedActivityId,
                isFilterSheetOpen = isFilterSheetOpen,
                isDatePickerOpen = isDatePickerOpen,
                isGroupByMenuOpen = isGroupByMenuOpen
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ActivityScreenControlState()
        )

    val uiState: StateFlow<ActivityScreenUiState> =
        combine(
            activityDetailsFlow,
            filterState,
            screenControlState
        ) { activityDetails, currentFilterState, controls ->

            val activities: List<ActivityUiModel> = activityDetails.map { detail: ActivityPeriodDetailModel ->
                detail.toPeriodUiModel()
            }

            val filteredActivities: List<ActivityUiModel> = activities
                .filterByTimeOfDay(currentFilterState.timeOfDayFilter)
                .filterByCategories(currentFilterState.selectedCategoryIds)
                .filterByLifeAreas(currentFilterState.selectedLifeAreaIds)

            val sections: List<ActivityListSectionUiModel> = filteredActivities.groupByMode(
                groupBy = currentFilterState.groupBy
            )

            ActivityScreenUiState(
                isLoading = false,
                errorMessage = null,

                filterState = currentFilterState,
                topBar = buildActivityTopBarUiState(
                    filterState = currentFilterState
                ),

                activities = filteredActivities,
                sections = sections,

                availableCategoryIds = activities
                    .mapNotNull { activity ->
                        activity.categoryId
                    }
                    .distinct()
                    .sorted(),

                availableLifeAreaIds = activities
                    .mapNotNull { activity ->
                        activity.lifeAreaId
                    }
                    .distinct()
                    .sorted(),

                selectedActivityId = controls.selectedActivityId,

                isFilterSheetOpen = controls.isFilterSheetOpen,
                isDatePickerOpen = controls.isDatePickerOpen,
                isGroupByMenuOpen = controls.isGroupByMenuOpen
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ActivityScreenUiState()
        )



    private fun currentSelectedDateString(): String {
        return filterState.value.selectedDate.toIsoDateString()
    }

    fun onPeriodModeChanged(
        mode: ActivityPeriodMode
    ) {
        filterState.update { currentState ->
            currentState.copy(
                periodMode = mode
            )
        }
    }

    fun onDateSelected(
        date: LocalDate
    ) {
        filterState.update { currentState ->
            currentState.copy(
                selectedDate = date
            )
        }

        dismissDatePicker()
    }

    fun goToPreviousPeriod() {
        filterState.update { currentState ->
            currentState.copy(
                selectedDate = when (currentState.periodMode) {
                    ActivityPeriodMode.DAY -> currentState.selectedDate.minusDays(1)
                    ActivityPeriodMode.WEEK -> currentState.selectedDate.minusWeeks(1)
                    ActivityPeriodMode.MONTH -> currentState.selectedDate.minusMonths(1)
                }
            )
        }
    }

    fun goToNextPeriod() {
        filterState.update { currentState ->
            currentState.copy(
                selectedDate = when (currentState.periodMode) {
                    ActivityPeriodMode.DAY -> currentState.selectedDate.plusDays(1)
                    ActivityPeriodMode.WEEK -> currentState.selectedDate.plusWeeks(1)
                    ActivityPeriodMode.MONTH -> currentState.selectedDate.plusMonths(1)
                }
            )
        }
    }

    fun goToToday() {
        filterState.update { currentState ->
            currentState.copy(
                selectedDate = LocalDate.now()
            )
        }
    }

    fun onTimeOfDayFilterChanged(
        filter: ActivityTimeOfDayFilter
    ) {
        filterState.update { currentState ->
            currentState.copy(
                timeOfDayFilter = filter
            )
        }
    }

    fun onCategoryCheckedChange(
        categoryId: String,
        checked: Boolean
    ) {
        filterState.update { currentState ->
            currentState.copy(
                selectedCategoryIds = currentState.selectedCategoryIds.toggle(
                    value = categoryId,
                    checked = checked
                )
            )
        }
    }

    fun onLifeAreaCheckedChange(
        lifeAreaId: String,
        checked: Boolean
    ) {
        filterState.update { currentState ->
            currentState.copy(
                selectedLifeAreaIds = currentState.selectedLifeAreaIds.toggle(
                    value = lifeAreaId,
                    checked = checked
                )
            )
        }
    }

    fun clearCategoryFilters() {
        filterState.update { currentState ->
            currentState.copy(
                selectedCategoryIds = emptySet()
            )
        }
    }

    fun clearLifeAreaFilters() {
        filterState.update { currentState ->
            currentState.copy(
                selectedLifeAreaIds = emptySet()
            )
        }
    }

    fun clearAllFilters() {
        filterState.update { currentState ->
            currentState.copy(
                timeOfDayFilter = ActivityTimeOfDayFilter.ANYTIME,
                selectedCategoryIds = emptySet(),
                selectedLifeAreaIds = emptySet(),
                groupBy = ActivityGroupBy.NONE
            )
        }
    }

    fun onGroupByChanged(
        groupBy: ActivityGroupBy
    ) {
        filterState.update { currentState ->
            currentState.copy(
                groupBy = groupBy
            )
        }

        dismissGroupByMenu()
    }

    // UI dialog/menu action
    fun openFilterSheet() {
        isFilterSheetOpen.value = true
    }

    fun dismissFilterSheet() {
        isFilterSheetOpen.value = false
    }

    fun openDatePicker() {
        isDatePickerOpen.value = true
    }

    fun dismissDatePicker() {
        isDatePickerOpen.value = false
    }

    fun openGroupByMenu() {
        isGroupByMenuOpen.value = true
    }

    fun dismissGroupByMenu() {
        isGroupByMenuOpen.value = false
    }
    //--------------------

    fun openProgressDialog(
        activityId: String
    ) {
        selectedActivityId.value = activityId
    }

    fun dismissProgressDialog() {
        selectedActivityId.value = null
    }

    fun completeYesNoActivity(
        activityId: String,
        note: String
    ) {
        viewModelScope.launch {
            runCatching {
                repository.updateActivityCompletion(
                    activityId = activityId,
                    date = currentSelectedDateString(),
                    isCompleted = true,
                    note = note
                )
            }.onSuccess {
                dismissProgressDialog()
            }.onFailure { throwable ->
                createActivityUiState.update {
                    it.copy(
                        errorMessage = throwable.message
                    )
                }
            }
        }
    }

    fun saveNumericProgress(
        activityId: String,
        value: Double,
        note: String
    ) {
        val activity = uiState.value.activities
            .firstOrNull { it.id == activityId }

        val shouldComplete = activity?.targetValue
            ?.let { targetValue ->
                targetValue > 0.0 && value >= targetValue
            } ?: false

        viewModelScope.launch {
            runCatching {
                repository.updateNumericProgress(
                    activityId = activityId,
                    date = currentSelectedDateString(),
                    value = value,
                    note = note
                )

                repository.updateActivityCompletion(
                    activityId = activityId,
                    date = currentSelectedDateString(),
                    isCompleted = shouldComplete,
                    note = note
                )
            }.onSuccess {
                dismissProgressDialog()
            }.onFailure { throwable ->
                createActivityUiState.update {
                    it.copy(
                        errorMessage = throwable.message
                    )
                }
            }
        }
    }

    fun saveChecklistProgress(
        activityId: String,
        completedSubtaskIds: Set<String>,
        note: String
    ) {
        val activity = uiState.value.activities
            .firstOrNull { it.id == activityId }

        val subtasks = activity?.subtasks.orEmpty()

        val shouldComplete = subtasks.isNotEmpty() &&
                subtasks.all { subtask ->
                    subtask.id in completedSubtaskIds
                }

        viewModelScope.launch {
            runCatching {
                subtasks.forEach { subtask ->
                    val newCompletedState = subtask.id in completedSubtaskIds

                    if (subtask.isCompleted != newCompletedState) {
                        repository.toggleSubtask(
                            subtaskId = subtask.id,
                            date = currentSelectedDateString(),
                            isCompleted = newCompletedState
                        )
                    }
                }

                repository.updateActivityCompletion(
                    activityId = activityId,
                    date = currentSelectedDateString(),
                    isCompleted = shouldComplete,
                    note = note
                )
            }.onSuccess {
                dismissProgressDialog()
            }.onFailure { throwable ->
                createActivityUiState.update {
                    it.copy(
                        errorMessage = throwable.message
                    )
                }
            }
        }
    }

    // Inside ActivityViewModel.kt

    fun onPreviousDate() {
        val currentDate = uiState.value.filterState.selectedDate
        // Depending on your app logic, you might want to subtract 1 day,
        // or subtract based on the current PeriodMode (Week/Month)
        val newDate = currentDate.minusDays(1)
        onDateSelected(newDate)
    }

    fun onNextDate() {
        val currentDate = uiState.value.filterState.selectedDate
        val newDate = currentDate.plusDays(1)
        onDateSelected(newDate)
    }

    fun onActivityCheckedChange(
        activityId: String,
        isCompleted: Boolean
    ) {
        viewModelScope.launch {
            runCatching {
                repository.updateActivityCompletion(
                    activityId = activityId,
                    date = currentSelectedDateString(),
                    isCompleted = isCompleted
                )
            }.onFailure { throwable ->
                // Giai đoạn MVP: lưu lỗi nhẹ.
                // Sau này có thể dùng Snackbar/Event channel.
                createActivityUiState.update {
                    it.copy(
                        errorMessage = throwable.message
                    )
                }
            }
        }
    }

    fun onNumericProgressChanged(
        activityId: String,
        value: Double
    ) {
        viewModelScope.launch {
            runCatching {
                repository.updateNumericProgress(
                    activityId = activityId,
                    date = currentSelectedDateString(),
                    value = value
                )
            }.onFailure { throwable ->
                createActivityUiState.update {
                    it.copy(
                        errorMessage = throwable.message
                    )
                }
            }
        }
    }

    fun onSubtaskCheckedChange(
        subtaskId: String,
        isCompleted: Boolean
    ) {
        viewModelScope.launch {
            runCatching {
                repository.toggleSubtask(
                    subtaskId = subtaskId,
                    date = currentSelectedDateString(),
                    isCompleted = isCompleted
                )
            }.onFailure { throwable ->
                createActivityUiState.update {
                    it.copy(
                        errorMessage = throwable.message
                    )
                }
            }
        }
    }

    fun onCreateActivity(
        formState: CreateActivityFormState
    ) {
        viewModelScope.launch {
            createActivityUiState.update {
                it.copy(
                    isSaving = true,
                    errorMessage = null,
                    isSaved = false
                )
            }

            runCatching {
                val now = System.currentTimeMillis()
                val activityId = UUID.randomUUID().toString()

                val activity = ActivityModel(
                    id = activityId,
                    title = formState.title,
                    description = formState.description,
                    iconName = formState.iconName,
                    colorHex = formState.colorHex,
                    activityType = formState.activityType,
                    trackingType = formState.trackingType,
                    measurableMode = formState.measurableMode,
                    targetValue = formState.targetValue,
                    unit = formState.unit,
                    categoryId = formState.categoryId,
                    lifeAreaId = formState.lifeAreaId,
                    timeOfDay = formState.timeOfDay,
                    specificTimeMinutes = formState.specificTimeMinutes,
                    repeatEnabled = formState.repeatEnabled,
                    repeatInterval = formState.repeatInterval,
                    repeatUnit = formState.repeatUnit,
                    repeatStartDate = formState.repeatStartDate,
                    repeatEndType = formState.repeatEndType,
                    repeatEndDate = formState.repeatEndDate,
                    repeatEndOccurrences = formState.repeatEndOccurrences,
                    createdAt = now,
                    updatedAt = now,
                    isArchived = false
                )

                val subtasks = formState.subtasks.mapIndexed { index, draft ->
                    ActivitySubtaskModel(
                        id = draft.id.ifBlank {
                            UUID.randomUUID().toString()
                        },
                        activityId = activityId,
                        title = draft.title,
                        orderIndex = index,
                        isCompleted = false,
                        createdAt = now,
                        updatedAt = now,
                        isArchived = false
                    )
                }

                repository.createActivity(
                    CreateActivityRequest(
                        activity = activity,
                        subtasks = subtasks
                    )
                )
            }.onSuccess {
                createActivityUiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = null,
                        isSaved = true
                    )
                }
            }.onFailure { throwable ->
                createActivityUiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = throwable.message,
                        isSaved = false
                    )
                }
            }
        }
    }

    fun onArchiveActivity(
        activityId: String
    ) {
        viewModelScope.launch {
            runCatching {
                repository.archiveActivity(
                    activityId = activityId
                )
            }.onFailure { throwable ->
                createActivityUiState.update {
                    it.copy(
                        errorMessage = throwable.message
                    )
                }
            }
        }
    }

    fun clearCreateActivitySavedState() {
        createActivityUiState.update {
            it.copy(
                isSaved = false
            )
        }
    }
}

private fun ActivityDetailModel.toUiModel(): ActivityUiModel {
    val isHabit = activity.activityType.name == "HABIT"

    val completedSubtaskCount = subtasks.count {
        it.isCompleted
    }

    val totalSubtaskCount = subtasks.size

    val checklistProgress = if (totalSubtaskCount > 0) {
        completedSubtaskCount.toFloat() / totalSubtaskCount.toFloat()
    } else {
        null
    }

    val numericProgress = if (
        activity.targetValue != null &&
        activity.targetValue > 0.0
    ) {
        ((log?.value ?: 0.0) / activity.targetValue)
            .toFloat()
            .coerceIn(0f, 1f)
    } else {
        null
    }

    val uiTrackingType = when (activity.trackingType.name) {
        "MEASURABLE" -> ActivityUiTrackingType.MEASURABLE
        else -> ActivityUiTrackingType.YES_NO
    }

    val uiMeasurableMode = when (activity.measurableMode?.name) {
        "NUMERIC" -> ActivityUiMeasurableMode.NUMERIC
        "CHECKLIST" -> ActivityUiMeasurableMode.CHECKLIST
        else -> null
    }

    val progress = when (uiMeasurableMode) {
        ActivityUiMeasurableMode.NUMERIC -> numericProgress
        ActivityUiMeasurableMode.CHECKLIST -> checklistProgress
        null -> null
    }

    val isChecklistCompleted = totalSubtaskCount > 0 &&
            completedSubtaskCount == totalSubtaskCount

    val isCompleted = when (uiMeasurableMode) {
        ActivityUiMeasurableMode.CHECKLIST -> {
            log?.isCompleted ?: isChecklistCompleted
        }

        else -> {
            log?.isCompleted ?: false
        }
    }

    return ActivityUiModel(
        id = activity.id,
        title = activity.title,
        description = activity.description,
        iconName = activity.iconName,
        colorHex = activity.colorHex,
        isHabit = isHabit,
        trackingType = uiTrackingType,
        measurableMode = uiMeasurableMode,
        isCompleted = isCompleted,

        category = activity.categoryId,
        lifeArea = activity.lifeAreaId,

        categoryId = activity.categoryId,
        categoryName = activity.categoryId,
        lifeAreaId = activity.lifeAreaId,
        lifeAreaName = activity.lifeAreaId,

        timeOfDay = activity.timeOfDay,
        specificTimeMinutes = activity.specificTimeMinutes,

        currentValue = log?.value,
        targetValue = activity.targetValue,
        unit = activity.unit,
        streakCount = null,
        dueText = activity.timeOfDay.name,
        repeatText = buildRepeatText(activity),
        progress = progress,
        completedSubtaskCount = completedSubtaskCount,
        totalSubtaskCount = totalSubtaskCount,
        subtasks = subtasks.map {
            ActivitySubtaskUiModel(
                id = it.id,
                title = it.title,
                isCompleted = it.isCompleted,
                orderIndex = it.orderIndex
            )
        }
    )
}
//mapper
private fun ActivityPeriodDetailModel.toPeriodUiModel(): ActivityUiModel {
    val isHabit = activity.activityType.name == "HABIT"

    val uiTrackingType = when (activity.trackingType.name) {
        "MEASURABLE" -> ActivityUiTrackingType.MEASURABLE
        else -> ActivityUiTrackingType.YES_NO
    }

    val uiMeasurableMode = when (activity.measurableMode?.name) {
        "NUMERIC" -> ActivityUiMeasurableMode.NUMERIC
        "CHECKLIST" -> ActivityUiMeasurableMode.CHECKLIST
        else -> null
    }

    return ActivityUiModel(
        id = activity.id,
        title = activity.title,
        description = activity.description,

        iconName = activity.iconName,
        colorHex = activity.colorHex,

        isHabit = isHabit,
        trackingType = uiTrackingType,
        measurableMode = uiMeasurableMode,

        isCompleted = summary.isCompleted,

        category = activity.categoryId,
        lifeArea = activity.lifeAreaId,

        categoryId = activity.categoryId,
        categoryName = activity.categoryId,
        lifeAreaId = activity.lifeAreaId,
        lifeAreaName = activity.lifeAreaId,

        timeOfDay = activity.timeOfDay,
        specificTimeMinutes = activity.specificTimeMinutes,

        currentValue = summary.totalValue,
        targetValue = summary.targetValue,
        unit = activity.unit,

        streakCount = null,

        dueText = activity.timeOfDay.name,
        repeatText = buildRepeatText(activity),

        progress = summary.progress,

        completedSubtaskCount = summary.completedSubtaskLogCount,
        totalSubtaskCount = summary.totalSubtaskPossibleCount,

        subtasks = subtasks.map { subtask ->
            ActivitySubtaskUiModel(
                id = subtask.id,
                title = subtask.title,
                isCompleted = subtask.isCompleted,
                orderIndex = subtask.orderIndex
            )
        }
    )
}

private fun buildRepeatText(
    activity: ActivityModel
): String? {
    if (!activity.repeatEnabled) return null

    val interval = activity.repeatInterval ?: 1
    val unit = activity.repeatUnit?.name ?: return null

    return when (unit) {
        "DAY" -> if (interval == 1) "Hằng ngày" else "Mỗi $interval ngày"
        "WEEK" -> if (interval == 1) "Hằng tuần" else "Mỗi $interval tuần"
        "MONTH" -> if (interval == 1) "Hằng tháng" else "Mỗi $interval tháng"
        "YEAR" -> if (interval == 1) "Hằng năm" else "Mỗi $interval năm"
        else -> null
    }
}

private fun LocalDate.toIsoDateString(): String {
    return toString()
}

private data class ActivityScreenControlState(
    val selectedActivityId: String? = null,
    val isFilterSheetOpen: Boolean = false,
    val isDatePickerOpen: Boolean = false,
    val isGroupByMenuOpen: Boolean = false
)

private fun Set<String>.toggle(
    value: String,
    checked: Boolean
): Set<String> {
    return if (checked) {
        this + value
    } else {
        this - value
    }
}