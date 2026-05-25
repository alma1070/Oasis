package com.almaslowcore.oasis.features.activity.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almaslowcore.oasis.features.activity.domain.model.ActivityDetailModel
import com.almaslowcore.oasis.features.activity.domain.model.ActivityModel
import com.almaslowcore.oasis.features.activity.domain.model.ActivitySubtaskModel
import com.almaslowcore.oasis.features.activity.domain.model.CreateActivityRequest
import com.almaslowcore.oasis.features.activity.domain.repository.ActivityRepository
import com.almaslowcore.oasis.features.activity.presentation.components.ActivityTab
import com.almaslowcore.oasis.features.activity.presentation.model.ActivitySubtaskUiModel
import com.almaslowcore.oasis.features.activity.presentation.model.ActivityUiMeasurableMode
import com.almaslowcore.oasis.features.activity.presentation.model.ActivityUiModel
import com.almaslowcore.oasis.features.activity.presentation.model.ActivityUiTrackingType
import com.almaslowcore.oasis.features.activity.presentation.model.CreateActivityFormState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import com.almaslowcore.oasis.features.activity.presentation.model.ActivityScreenUiState
import com.almaslowcore.oasis.features.activity.presentation.model.CreateActivityUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val repository: ActivityRepository
) : ViewModel() {

    private val selectedTab = MutableStateFlow(ActivityTab.Today)

    private val selectedDate = MutableStateFlow(todayString())

    private val selectedActivityId = MutableStateFlow<String?>(null)
    private val createActivityUiState = MutableStateFlow(
        CreateActivityUiState()
    )

    val createState: StateFlow<CreateActivityUiState> = createActivityUiState

    val uiState: StateFlow<ActivityScreenUiState> =
        combine(
            repository.observeActivitiesForDate(selectedDate.value),
            selectedTab,
            selectedActivityId
        ) { activityDetails, tab, selectedActivityId ->
            val activities = activityDetails
                .map { detail ->
                    detail.toUiModel()
                }
                .filterByTab(tab)

            ActivityScreenUiState(
                isLoading = false,
                errorMessage = null,
                selectedTab = tab,
                activities = activities,
                selectedActivityId = selectedActivityId
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ActivityScreenUiState()
        )

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
                    date = selectedDate.value,
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
                    date = selectedDate.value,
                    value = value,
                    note = note
                )

                repository.updateActivityCompletion(
                    activityId = activityId,
                    date = selectedDate.value,
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
                            date = selectedDate.value,
                            isCompleted = newCompletedState
                        )
                    }
                }

                repository.updateActivityCompletion(
                    activityId = activityId,
                    date = selectedDate.value,
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

    fun onTabSelected(
        tab: ActivityTab
    ) {
        selectedTab.value = tab
    }

    fun onActivityCheckedChange(
        activityId: String,
        isCompleted: Boolean
    ) {
        viewModelScope.launch {
            runCatching {
                repository.updateActivityCompletion(
                    activityId = activityId,
                    date = selectedDate.value,
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
                    date = selectedDate.value,
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
                    date = selectedDate.value,
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

private fun List<ActivityUiModel>.filterByTab(
    tab: ActivityTab
): List<ActivityUiModel> {
    return when (tab) {
        ActivityTab.Today -> {
            filter {
                !it.isCompleted
            }
        }

        ActivityTab.Habits -> {
            filter {
                it.isHabit && !it.isCompleted
            }
        }

        ActivityTab.Tasks -> {
            filter {
                !it.isHabit && !it.isCompleted
            }
        }

        ActivityTab.Completed -> {
            filter {
                it.isCompleted
            }
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

private fun todayString(): String {
    return SimpleDateFormat(
        "yyyy-MM-dd",
        Locale.US
    ).format(Date())
}