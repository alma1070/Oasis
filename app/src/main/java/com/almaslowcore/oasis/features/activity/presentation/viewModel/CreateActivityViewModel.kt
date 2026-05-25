package com.almaslowcore.oasis.features.activity.presentation.viewModel

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almaslowcore.oasis.features.activity.domain.model.ActivityModel
import com.almaslowcore.oasis.features.activity.domain.model.ActivitySubtaskModel
import com.almaslowcore.oasis.features.activity.domain.model.ActivityTrackingType
import com.almaslowcore.oasis.features.activity.domain.model.ActivityType
import com.almaslowcore.oasis.features.activity.domain.model.CreateActivityRequest
import com.almaslowcore.oasis.features.activity.domain.model.MeasurableMode
import com.almaslowcore.oasis.features.activity.domain.model.RepeatEndType
import com.almaslowcore.oasis.features.activity.domain.model.RepeatUnit
import com.almaslowcore.oasis.features.activity.domain.model.TimeOfDay
import com.almaslowcore.oasis.features.activity.domain.repository.ActivityRepository
import com.almaslowcore.oasis.features.activity.presentation.model.CreateActivityFormState
import com.almaslowcore.oasis.features.activity.presentation.model.CreateActivitySubtaskDraft
import com.almaslowcore.oasis.features.activity.presentation.model.CreateActivityUiState
import com.almaslowcore.oasis.features.activity.presentation.model.CreateActivityValidationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@HiltViewModel
class CreateActivityViewModel @Inject constructor(
    private val activityRepository: ActivityRepository
) : ViewModel() {

    private val initialFormState = CreateActivityFormState(
        repeatStartDate = todayIsoDate()
    )

    private val _uiState = MutableStateFlow(
        CreateActivityUiState(
            formState = initialFormState,
            validationResult = validateCreateActivityForm(initialFormState)
        )
    )

    val uiState: StateFlow<CreateActivityUiState> = _uiState.asStateFlow()

    fun onIconSelected(iconName: String) {
        updateForm {
            it.copy(iconName = iconName)
        }
    }

    val titleTextState = TextFieldState()
    val descriptionTextState = TextFieldState()

    val targetValueTextState = TextFieldState()
    val subtaskInputTextState = TextFieldState()

    val repeatIntervalTextState = TextFieldState(initialText = "1")
    val repeatStartDateTextState = TextFieldState(initialText = todayIsoDate())
    val repeatEndDateTextState = TextFieldState()
    val repeatEndOccurrencesTextState = TextFieldState()

    init {
        viewModelScope.launch {
            snapshotFlow {
                currentTextFieldSnapshot()
            }.collect { textSnapshot ->
                _uiState.update { currentState ->
                    val newFormState = currentState.formState.copy(
                        title = textSnapshot.title,
                        description = textSnapshot.description,
                        targetValueText = textSnapshot.targetValueText,
                        subtaskInput = textSnapshot.subtaskInput,
                        repeatIntervalText = textSnapshot.repeatIntervalText,
                        repeatStartDate = textSnapshot.repeatStartDate,
                        repeatEndDate = textSnapshot.repeatEndDate,
                        repeatEndOccurrencesText = textSnapshot.repeatEndOccurrencesText
                    )

                    currentState.copy(
                        formState = newFormState,
                        validationResult = validateCreateActivityForm(newFormState),
                        errorMessage = null,
                        isSaved = false
                    )
                }
            }
        }
    }

    private fun currentTextFieldSnapshot(): CreateActivityTextFieldSnapshot {
        return CreateActivityTextFieldSnapshot(
            title = titleTextState.text.toString(),
            description = descriptionTextState.text.toString(),
            targetValueText = targetValueTextState.text.toString(),
            subtaskInput = subtaskInputTextState.text.toString(),
            repeatIntervalText = repeatIntervalTextState.text.toString(),
            repeatStartDate = repeatStartDateTextState.text.toString(),
            repeatEndDate = repeatEndDateTextState.text.toString(),
            repeatEndOccurrencesText = repeatEndOccurrencesTextState.text.toString()
        )
    }

    private fun currentFormState(): CreateActivityFormState {
        val textSnapshot = currentTextFieldSnapshot()

        return _uiState.value.formState.copy(
            title = textSnapshot.title,
            description = textSnapshot.description,
            targetValueText = textSnapshot.targetValueText,
            subtaskInput = textSnapshot.subtaskInput,
            repeatIntervalText = textSnapshot.repeatIntervalText,
            repeatStartDate = textSnapshot.repeatStartDate,
            repeatEndDate = textSnapshot.repeatEndDate,
            repeatEndOccurrencesText = textSnapshot.repeatEndOccurrencesText
        )
    }


    fun onActivityTypeChange(activityType: ActivityType) {
        updateForm {
            it.copy(activityType = activityType)
        }
    }

    fun onTrackingTypeChange(trackingType: ActivityTrackingType) {
        updateForm { state ->
            when (trackingType) {
                ActivityTrackingType.YES_NO -> {
                    targetValueTextState.clearText()
                    subtaskInputTextState.clearText()
                    state.copy(
                        trackingType = trackingType,
                        measurableMode = null,
                        targetValueText = "",
                        unit = "",
                        subtasks = emptyList(),
                        subtaskInput = ""
                    )
                }

                ActivityTrackingType.MEASURABLE -> {
                    state.copy(
                        trackingType = trackingType,
                        measurableMode = state.measurableMode ?: MeasurableMode.NUMERIC
                    )
                }
            }
        }
    }

    fun onMeasurableModeChange(measurableMode: MeasurableMode) {
        updateForm { state ->
            when (measurableMode) {
                MeasurableMode.NUMERIC -> {
                    subtaskInputTextState.clearText()
                    state.copy(
                        measurableMode = measurableMode,
                        subtasks = emptyList(),
                        subtaskInput = ""
                    )
                }

                MeasurableMode.CHECKLIST -> {
                    targetValueTextState.clearText()
                    state.copy(
                        measurableMode = measurableMode,
                        targetValueText = "",
                        unit = ""
                    )
                }
            }
        }
    }


    fun onUnitChange(unit: String) {
        updateForm {
            it.copy(unit = unit)
        }
    }

    fun addSubtask() {
        updateForm { state ->
            val cleanTitle = subtaskInputTextState.text.toString().trim()

            if (cleanTitle.isBlank()) {
                state
            } else {
                subtaskInputTextState.clearText()

                state.copy(
                    subtasks = state.subtasks + CreateActivitySubtaskDraft(
                        id = UUID.randomUUID().toString(),
                        title = cleanTitle,
                        orderIndex = state.subtasks.size
                    ),
                    subtaskInput = ""
                )
            }
        }
    }

    fun deleteSubtask(subtaskId: String) {
        updateForm { state ->
            val updatedSubtasks = state.subtasks
                .filterNot { it.id == subtaskId }
                .mapIndexed { index, subtask ->
                    subtask.copy(orderIndex = index)
                }

            state.copy(
                subtasks = updatedSubtasks
            )
        }
    }

    fun onCategorySelected(categoryId: String?) {
        updateForm {
            it.copy(categoryId = categoryId)
        }
    }

    fun onLifeAreaSelected(lifeAreaId: String?) {
        updateForm {
            it.copy(lifeAreaId = lifeAreaId)
        }
    }

    fun onTimeOfDayChange(timeOfDay: TimeOfDay) {
        updateForm { state ->
            state.copy(
                timeOfDay = timeOfDay,
                specificTimeMinutes = if (timeOfDay == TimeOfDay.SPECIFIC_TIME) {
                    state.specificTimeMinutes
                } else {
                    null
                }
            )
        }
    }

    fun onSpecificTimeSelected(minutes: Int) {
        updateForm {
            it.copy(specificTimeMinutes = minutes)
        }
    }

    fun onRepeatEnabledChange(enabled: Boolean) {
        if (enabled && repeatStartDateTextState.text.isBlank()) {
            repeatStartDateTextState.setTextAndPlaceCursorAtEnd(todayIsoDate())
        }
        updateForm { state ->
            state.copy(
                repeatEnabled = enabled,
                repeatStartDate = repeatStartDateTextState.text.toString()
            )
        }
    }

    fun onRepeatUnitChange(unit: RepeatUnit?) {
        updateForm {
            it.copy(repeatUnit = unit)
        }
    }

    fun onRepeatEndTypeChange(type: RepeatEndType) {
        updateForm {
            it.copy(repeatEndType = type)
        }
    }

    fun saveActivity() {
        val formState = currentFormState()
        val validation = validateCreateActivityForm(formState)

        if (!validation.isValid) {
            _uiState.update {
                it.copy(validationResult = validation)
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSaving = true,
                    errorMessage = null
                )
            }

            try {
                activityRepository.createActivity(
                    request = formState.toCreateActivityRequest()
                )

                _uiState.update {
                    it.copy(
                        isSaving = false,
                        isSaved = true
                    )
                }
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = exception.message ?: "Cannot create activity."
                    )
                }
            }
        }
    }

    private fun updateForm(
        transform: (CreateActivityFormState) -> CreateActivityFormState
    ) {
        _uiState.update { currentState ->
            val newFormState = transform(currentFormState())

            currentState.copy(
                formState = newFormState,
                validationResult = validateCreateActivityForm(newFormState),
                errorMessage = null,
                isSaved = false
            )
        }
    }
}

private fun validateCreateActivityForm(
    state: CreateActivityFormState
): CreateActivityValidationResult {
    val titleError = if (state.title.isBlank()) {
        "Title is required."
    } else {
        null
    }

    val targetValueError = if (
        state.trackingType == ActivityTrackingType.MEASURABLE &&
        state.measurableMode == MeasurableMode.NUMERIC &&
        ((state.targetValue ?: 0.0) <= 0.0)
    ) {
        "Target value must be greater than 0."
    } else {
        null
    }

    val unitError = if (
        state.trackingType == ActivityTrackingType.MEASURABLE &&
        state.measurableMode == MeasurableMode.NUMERIC &&
        state.unit.isBlank()
    ) {
        "Unit is required."
    } else {
        null
    }

    val subtaskError = if (
        state.trackingType == ActivityTrackingType.MEASURABLE &&
        state.measurableMode == MeasurableMode.CHECKLIST
    ) {
        when {
            state.subtasks.isEmpty() -> "Add at least one subtask."
            state.subtasks.any { it.title.isBlank() } -> "Subtask title cannot be empty."
            else -> null
        }
    } else {
        null
    }

    val specificTimeError = if (
        state.timeOfDay == TimeOfDay.SPECIFIC_TIME &&
        state.specificTimeMinutes == null
    ) {
        "Specific time is required."
    } else {
        null
    }

    val repeatIntervalError = if (
        state.repeatEnabled &&
        ((state.repeatInterval ?: 0) <= 0)
    ) {
        "Repeat interval must be greater than 0."
    } else {
        null
    }

    val repeatUnitError = if (
        state.repeatEnabled &&
        state.repeatUnit == null
    ) {
        "Repeat unit is required."
    } else {
        null
    }

    val repeatStartDateError = if (
        state.repeatEnabled &&
        state.repeatStartDate.isBlank()
    ) {
        "Start date is required."
    } else {
        null
    }

    val repeatEndDateError = if (
        state.repeatEnabled &&
        state.repeatEndType == RepeatEndType.ON_DATE &&
        state.repeatEndDate.isBlank()
    ) {
        "End date is required."
    } else {
        null
    }

    val repeatEndOccurrencesError = if (
        state.repeatEnabled &&
        state.repeatEndType == RepeatEndType.AFTER_OCCURRENCES &&
        ((state.repeatEndOccurrences ?: 0) <= 0)
    ) {
        "Occurrences must be greater than 0."
    } else {
        null
    }

    val isValid = listOf(
        titleError,
        targetValueError,
        unitError,
        subtaskError,
        specificTimeError,
        repeatIntervalError,
        repeatUnitError,
        repeatStartDateError,
        repeatEndDateError,
        repeatEndOccurrencesError
    ).all { it == null }

    return CreateActivityValidationResult(
        isValid = isValid,
        titleError = titleError,
        targetValueError = targetValueError,
        unitError = unitError,
        subtaskError = subtaskError,
        specificTimeError = specificTimeError,
        repeatIntervalError = repeatIntervalError,
        repeatUnitError = repeatUnitError,
        repeatStartDateError = repeatStartDateError,
        repeatEndDateError = repeatEndDateError,
        repeatEndOccurrencesError = repeatEndOccurrencesError
    )
}

private fun CreateActivityFormState.toCreateActivityRequest(): CreateActivityRequest {
    val now = System.currentTimeMillis()
    val activityId = UUID.randomUUID().toString()

    val shouldUseNumeric = trackingType == ActivityTrackingType.MEASURABLE &&
            measurableMode == MeasurableMode.NUMERIC

    val shouldUseChecklist = trackingType == ActivityTrackingType.MEASURABLE &&
            measurableMode == MeasurableMode.CHECKLIST

    val activity = ActivityModel(
        id = activityId,
        title = title.trim(),
        description = description.trim().ifBlank { null },
        iconName = iconName,
        colorHex = colorHex,

        activityType = activityType,
        trackingType = trackingType,
        measurableMode = if (trackingType == ActivityTrackingType.MEASURABLE) {
            measurableMode
        } else {
            null
        },

        targetValue = if (shouldUseNumeric) targetValue else null,
        unit = if (shouldUseNumeric) unit.trim().ifBlank { null } else null,

        categoryId = categoryId,
        lifeAreaId = lifeAreaId,

        timeOfDay = timeOfDay,
        specificTimeMinutes = if (timeOfDay == TimeOfDay.SPECIFIC_TIME) {
            specificTimeMinutes
        } else {
            null
        },

        repeatEnabled = repeatEnabled,
        repeatInterval = if (repeatEnabled) repeatInterval else null,
        repeatUnit = if (repeatEnabled) repeatUnit else null,
        repeatStartDate = if (repeatEnabled) repeatStartDate.trim().ifBlank { null } else null,
        repeatEndType = if (repeatEnabled) repeatEndType else RepeatEndType.NEVER,
        repeatEndDate = if (repeatEnabled && repeatEndType == RepeatEndType.ON_DATE) {
            repeatEndDate.trim().ifBlank { null }
        } else {
            null
        },
        repeatEndOccurrences = if (repeatEnabled && repeatEndType == RepeatEndType.AFTER_OCCURRENCES) {
            repeatEndOccurrences
        } else {
            null
        },

        createdAt = now,
        updatedAt = now
    )

    val subtaskModels = if (shouldUseChecklist) {
        subtasks.mapIndexed { index, subtask ->
            ActivitySubtaskModel(
                id = subtask.id,
                activityId = activityId,
                title = subtask.title,
                orderIndex = index,
                createdAt = now,
                updatedAt = now
            )
        }
    } else {
        emptyList()
    }

    return CreateActivityRequest(
        activity = activity,
        subtasks = subtaskModels
    )
}

private fun todayIsoDate(): String {
    return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
}

private data class CreateActivityTextFieldSnapshot(
    val title: String,
    val description: String,
    val targetValueText: String,
    val subtaskInput: String,
    val repeatIntervalText: String,
    val repeatStartDate: String,
    val repeatEndDate: String,
    val repeatEndOccurrencesText: String
)