package com.almaslowcore.oasis.features.activity.presentation.screen

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.almaslowcore.oasis.features.activity.domain.model.ActivityTrackingType
import com.almaslowcore.oasis.features.activity.domain.model.ActivityType
import com.almaslowcore.oasis.features.activity.domain.model.MeasurableMode
import com.almaslowcore.oasis.features.activity.domain.model.RepeatEndType
import com.almaslowcore.oasis.features.activity.domain.model.RepeatUnit
import com.almaslowcore.oasis.features.activity.domain.model.TimeOfDay
import com.almaslowcore.oasis.features.activity.presentation.model.CreateActivitySubtaskDraft
import com.almaslowcore.oasis.features.activity.presentation.model.CreateActivityUiState
import com.almaslowcore.oasis.features.activity.presentation.viewmodel.CreateActivityViewModel
import com.almaslowcore.oasis.ui.components.OasisFilterChipGroup
import com.almaslowcore.oasis.ui.components.OasisToggle
import com.almaslowcore.oasis.ui.components.buttons.OasisRadioButtonGroup
import com.almaslowcore.oasis.ui.components.dialogs.DatePickerModal
import com.almaslowcore.oasis.ui.components.dialogs.OasisTimePicker
import com.almaslowcore.oasis.ui.components.inputs.OasisDropdown
import com.almaslowcore.oasis.ui.components.inputs.OasisTextField
import com.almaslowcore.oasis.ui.components.layout.OasisScreen
import com.almaslowcore.oasis.ui.components.layout.SectionHeader
import com.almaslowcore.oasis.ui.navigation.BottomBarAction
import com.almaslowcore.oasis.ui.navigation.BottomBarConfig
import com.almaslowcore.oasis.ui.navigation.LocalBottomBarController
import java.time.Instant
import java.time.ZoneId
import kotlin.text.lowercase
import com.almaslowcore.oasis.R

@Composable
fun CreateActivityRoute(
    onNavigateBack: () -> Unit,
    viewModel: CreateActivityViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val bottomBarController = LocalBottomBarController.current

    DisposableEffect(
        uiState.validationResult.isValid,
        uiState.isSaving
    ) {
        bottomBarController.updateConfig(
            BottomBarConfig(
                actions = listOf(
                    BottomBarAction(
                        label = "Cancel",
                        onClick = onNavigateBack
                    ),
                    BottomBarAction(
                        label = if (uiState.isSaving) "Saving..." else "Save",
                        icon = Icons.Default.Done,
                        enabled = uiState.validationResult.isValid && !uiState.isSaving,
                        isPrimary = true,
                        onClick = viewModel::saveActivity
                    )
                )
            )
        )

        onDispose {
            bottomBarController.clear()
        }
    }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onNavigateBack()
        }
    }

    CreateActivityScreen(
        uiState = uiState,
        titleTextState = viewModel.titleTextState,
        descriptionTextState = viewModel.descriptionTextState,
        targetValueTextState = viewModel.targetValueTextState,
        subtaskInputTextState = viewModel.subtaskInputTextState,
        repeatIntervalTextState = viewModel.repeatIntervalTextState,
        repeatStartDateTextState = viewModel.repeatStartDateTextState,
        repeatEndDateTextState = viewModel.repeatEndDateTextState,
        repeatEndOccurrencesTextState = viewModel.repeatEndOccurrencesTextState,

        onActivityTypeChange = viewModel::onActivityTypeChange,
        onTrackingTypeChange = viewModel::onTrackingTypeChange,
        onMeasurableModeChange = viewModel::onMeasurableModeChange,
        onUnitChange = viewModel::onUnitChange,

        onAddSubtask = viewModel::addSubtask,
        onDeleteSubtask = viewModel::deleteSubtask,

        onCategorySelected = viewModel::onCategorySelected,
        onLifeAreaSelected = viewModel::onLifeAreaSelected,

        onTimeOfDayChange = viewModel::onTimeOfDayChange,
        onSpecificTimeSelected = viewModel::onSpecificTimeSelected,

        onRepeatEnabledChange = viewModel::onRepeatEnabledChange,
        onRepeatUnitChange = viewModel::onRepeatUnitChange,
        onRepeatEndTypeChange = viewModel::onRepeatEndTypeChange
    )
}


@Composable
fun CreateActivityScreen(
    uiState: CreateActivityUiState,

    titleTextState: TextFieldState,
    descriptionTextState: TextFieldState,
    targetValueTextState: TextFieldState,
    subtaskInputTextState: TextFieldState,
    repeatIntervalTextState: TextFieldState,
    repeatStartDateTextState: TextFieldState,
    repeatEndDateTextState: TextFieldState,
    repeatEndOccurrencesTextState: TextFieldState,

    onActivityTypeChange: (ActivityType) -> Unit,
    onTrackingTypeChange: (ActivityTrackingType) -> Unit,
    onMeasurableModeChange: (MeasurableMode) -> Unit,

    onUnitChange: (String) -> Unit,

    onAddSubtask: () -> Unit,
    onDeleteSubtask: (String) -> Unit,

    onCategorySelected: (String?) -> Unit,
    onLifeAreaSelected: (String?) -> Unit,

    onTimeOfDayChange: (TimeOfDay) -> Unit,
    onSpecificTimeSelected: (Int) -> Unit,

    onRepeatEnabledChange: (Boolean) -> Unit,
    onRepeatUnitChange: (RepeatUnit?) -> Unit,
    onRepeatEndTypeChange: (RepeatEndType) -> Unit
) {
    val formState = uiState.formState
    val validation = uiState.validationResult
    // State for Dialog Visibility
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    OasisScreen(
        scrollable = true
    ) {
        BasicInfoSection(
            titleTextState = titleTextState,
            titleError = validation.titleError,
            descriptionTextState = descriptionTextState
        )

        TypeSection(
            activityType = formState.activityType,
            onActivityTypeChange = onActivityTypeChange,
            trackingType = formState.trackingType,
            onTrackingTypeChange = onTrackingTypeChange
        )

        if (formState.trackingType == ActivityTrackingType.MEASURABLE) {
            MeasurableSection(
                measurableMode = formState.measurableMode,
                onMeasurableModeChange = onMeasurableModeChange,
                targetValueTextState = targetValueTextState,
                targetValueError = validation.targetValueError,
                unit = formState.unit,
                onUnitChange = onUnitChange,
                unitError = validation.unitError,
                subtaskInputTextState = subtaskInputTextState,
                subtasks = formState.subtasks,
                onAddSubtask = onAddSubtask,
                onDeleteSubtask = onDeleteSubtask,
                subtaskError = validation.subtaskError
            )
        }

        ClassificationSection(
            categoryId = formState.categoryId,
            onCategorySelected = onCategorySelected,
            lifeAreaId = formState.lifeAreaId,
            onLifeAreaSelected = onLifeAreaSelected
        )

        TimeSection(
            timeOfDay = formState.timeOfDay,
            onTimeOfDayChange = onTimeOfDayChange,
            specificTimeMinutes = formState.specificTimeMinutes,
            specificTimeError = validation.specificTimeError,
            onTimeClick = { showStartTimePicker = true }
        )

        RepeatSection(
            repeatEnabled = formState.repeatEnabled,
            onRepeatEnabledChange = onRepeatEnabledChange,
            repeatIntervalTextState = repeatIntervalTextState,
            repeatIntervalError = validation.repeatIntervalError,
            repeatUnit = formState.repeatUnit,
            onRepeatUnitChange = onRepeatUnitChange,
            repeatUnitError = validation.repeatUnitError,
            repeatStartDateTextState = repeatStartDateTextState,
            repeatStartDateError = validation.repeatStartDateError,
            repeatEndType = formState.repeatEndType,
            onRepeatEndTypeChange = onRepeatEndTypeChange,
            repeatEndDateTextState = repeatEndDateTextState,
            repeatEndDateError = validation.repeatEndDateError,
            repeatEndOccurrencesTextState = repeatEndOccurrencesTextState,
            repeatEndOccurrencesError = validation.repeatEndOccurrencesError,
            onStartDateClick = { showStartDatePicker = true },
            onEndDateClick = { showEndDatePicker = true }
        )

        // Dialogs
        if (showStartTimePicker) {
            OasisTimePicker(
                initialMinutes = formState.specificTimeMinutes ?: 0,
                onDismiss = { showStartTimePicker = false },
                onConfirm = { minutes: Int ->
                    onSpecificTimeSelected(minutes)
                    showStartTimePicker = false
                }
            )
        }

        if (showStartDatePicker) {
            DatePickerModal(
                onDateSelected = { millis ->
                    millis?.let {
                        val date = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                        repeatStartDateTextState.edit { replace(0, length, date.toString()) }
                    }
                },
                onDismiss = { showStartDatePicker = false }
            )
        }

        if (showEndDatePicker) {
            DatePickerModal(
                onDateSelected = { millis ->
                    millis?.let {
                        val date = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                        repeatEndDateTextState.edit { replace(0, length, date.toString()) }
                    }
                },
                onDismiss = { showEndDatePicker = false }
            )
        }

        uiState.errorMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun BasicInfoSection(
    titleTextState: TextFieldState,
    titleError: String?,
    descriptionTextState: TextFieldState
) {
    OasisTextField(
        modifier = Modifier.fillMaxWidth(),
        state = titleTextState,
        label = stringResource(R.string.title),
        errorText = titleError,
        lineLimits = TextFieldLineLimits.SingleLine
    )

    OasisTextField(
        modifier = Modifier.fillMaxWidth(),
        state = descriptionTextState,
        label = stringResource(R.string.description),
        lineLimits = TextFieldLineLimits.MultiLine(
            minHeightInLines = 3,
            maxHeightInLines = 5
        )
    )
}

@Composable
private fun TypeSection(
    activityType: ActivityType,
    onActivityTypeChange: (ActivityType) -> Unit,
    trackingType: ActivityTrackingType,
    onTrackingTypeChange: (ActivityTrackingType) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OasisRadioButtonGroup(
            label = "Activity type",
            options = ActivityType.entries,
            selectedOption = activityType,
            onOptionSelected = onActivityTypeChange,
            optionToString = { it.name.lowercase().replaceFirstChar { char -> char.uppercase() } }
        )

        OasisRadioButtonGroup(
            label = "Tracking type",
            options = ActivityTrackingType.entries,
            selectedOption = trackingType,
            onOptionSelected = onTrackingTypeChange,
            optionToString = {
                when(it) {
                    ActivityTrackingType.YES_NO -> "${stringResource(R.string.yes)} / ${stringResource(R.string.no)}"
                    ActivityTrackingType.MEASURABLE -> stringResource(R.string.measurable)
                }
            }
        )
    }
}

@Composable
private fun MeasurableSection(
    measurableMode: MeasurableMode?,
    onMeasurableModeChange: (MeasurableMode) -> Unit,
    targetValueTextState: TextFieldState,
    targetValueError: String?,
    unit: String,
    onUnitChange: (String) -> Unit,
    unitError: String?,
    subtaskInputTextState: TextFieldState,
    subtasks: List<CreateActivitySubtaskDraft>,
    onAddSubtask: () -> Unit,
    onDeleteSubtask: (String) -> Unit,
    subtaskError: String?
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OasisRadioButtonGroup(
            label = "Measurable mode",
            options = MeasurableMode.entries,
            selectedOption = measurableMode,
            onOptionSelected = onMeasurableModeChange,
            optionToString = { it.name.lowercase().replaceFirstChar { char -> char.uppercase() } }
        )

        when (measurableMode) {
            MeasurableMode.NUMERIC -> {
                OasisTextField(
                    modifier = Modifier.fillMaxWidth(),
                    state = targetValueTextState,
                    label = "Target value",
                    errorText = targetValueError,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    lineLimits = TextFieldLineLimits.SingleLine
                )

                OasisDropdown(
                    label = "Unit",
                    options = listOf("ml", "pages", "minutes", "steps", "tasks"),
                    selectedOption = unit,
                    onOptionSelected = onUnitChange,
                    errorText = unitError
                )
            }

            MeasurableMode.CHECKLIST -> {
                OasisTextField(
                    modifier = Modifier.fillMaxWidth(),
                    state = subtaskInputTextState,
                    label = "Add subtask",
                    lineLimits = TextFieldLineLimits.SingleLine,
                    trailingIcon = {
                        TextButton(
                            onClick = onAddSubtask
                        ) {
                            Text("Add")
                        }
                    }
                )

                subtaskError?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                if (subtasks.isNotEmpty()) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        subtasks.sortedBy { it.orderIndex }.forEach { subtask ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    modifier = Modifier.weight(1f),
                                    text = "${subtask.orderIndex + 1}. ${subtask.title}",
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                TextButton(
                                    onClick = {
                                        onDeleteSubtask(subtask.id)
                                    }
                                ) {
                                    Text("Delete")
                                }
                            }
                        }
                    }
                }
            }

            null -> {
                Text(
                    text = "Choose a measurable mode.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ClassificationSection(
    categoryId: String?,
    onCategorySelected: (String?) -> Unit,
    lifeAreaId: String?,
    onLifeAreaSelected: (String?) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OasisDropdown(
            label = "Category",
            options = listOf("Study", "Health", "Project", "Mind", "Home"),
            selectedOption = categoryId ?: "", // Handle null by passing empty string
            onOptionSelected = { onCategorySelected(it) }
        )

        OasisDropdown(
            label = "Life area",
            options = listOf("Personal Growth", "Health", "Career", "Mind", "Environment"),
            selectedOption = lifeAreaId ?: "",
            onOptionSelected = { onLifeAreaSelected(it) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimeSection(
    timeOfDay: TimeOfDay,
    onTimeOfDayChange: (TimeOfDay) -> Unit,
    specificTimeMinutes: Int?,
    specificTimeError: String?,
    onTimeClick: () -> Unit
) {

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OasisFilterChipGroup(
            options = TimeOfDay.entries,
            selectedOption = timeOfDay,
            onOptionSelected = onTimeOfDayChange,
            optionToString = { it.name.lowercase().replaceFirstChar { char -> char.uppercase() } }
        )

        if (timeOfDay == TimeOfDay.SPECIFIC_TIME) {
            // Interaction source to detect clicks on the text field
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()

            // Launch the picker when the field is pressed
            LaunchedEffect(isPressed) {
                if (isPressed) onTimeClick()
            }

            // Format the minutes (e.g., 90 -> "01:30")
            val displayTime = remember(specificTimeMinutes) {
                specificTimeMinutes?.let {
                    val hours = it / 60
                    val mins = it % 60
                    "%02d:%02d".format(hours, mins)
                } ?: ""
            }

            // Note: We use a local TextFieldState for display purposes
            // since the actual source of truth is the Int in the ViewModel
            val timeTextState = remember { TextFieldState(displayTime) }

            // Keep the text field text in sync with the formatted specificTimeMinutes
            LaunchedEffect(displayTime) {
                timeTextState.edit {
                    replace(0, length, displayTime)
                }
            }

            OasisTextField(
                state = timeTextState,
                label = "Select Time",
                readOnly = true, // Prevent keyboard from opening
                interactionSource = interactionSource,
                errorText = specificTimeError,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun RepeatSection(
    repeatEnabled: Boolean,
    onRepeatEnabledChange: (Boolean) -> Unit,
    repeatIntervalTextState: TextFieldState,
    repeatIntervalError: String?,
    repeatUnit: RepeatUnit?,
    onRepeatUnitChange: (RepeatUnit?) -> Unit,
    repeatUnitError: String?,
    repeatStartDateTextState: TextFieldState,
    repeatStartDateError: String?,
    onStartDateClick: () -> Unit,
    repeatEndType: RepeatEndType,
    onRepeatEndTypeChange: (RepeatEndType) -> Unit,
    repeatEndDateTextState: TextFieldState,
    repeatEndDateError: String?,
    onEndDateClick: () -> Unit,
    repeatEndOccurrencesTextState: TextFieldState,
    repeatEndOccurrencesError: String?
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OasisToggle(
            label = "Repeat activity",
            description = "Set a schedule for this activity",
            checked = repeatEnabled,
            onCheckedChange = onRepeatEnabledChange
        )

        if (repeatEnabled) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OasisTextField(
                    modifier = Modifier.weight(1f),
                    state = repeatIntervalTextState,
                    label = "Every",
                    errorText = repeatIntervalError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                OasisDropdown(
                    modifier = Modifier.weight(1f),
                    label = "Repeat unit",
                    options = RepeatUnit.entries,
                    selectedOption = repeatUnit ?: RepeatUnit.DAY,
                    onOptionSelected = { onRepeatUnitChange(it) },
                    optionToString = { it.name.lowercase().replaceFirstChar { char -> char.uppercase() } },
                    errorText = repeatUnitError
                )
            }

            // Use InteractionSource to detect clicks on read-only fields
            val startInteractionSource = remember { MutableInteractionSource() }
            if (startInteractionSource.collectIsPressedAsState().value) {
                LaunchedEffect(Unit) { onStartDateClick() }
            }

            val endInteractionSource = remember { MutableInteractionSource() }
            if (endInteractionSource.collectIsPressedAsState().value) {
                LaunchedEffect(Unit) { onEndDateClick() }
            }

            OasisTextField(
                state = repeatStartDateTextState,
                label = "Start Date",
                readOnly = true,
                errorText = repeatStartDateError,
                interactionSource = startInteractionSource
            )

            // NEW: Using OasisRadioButtonGroup for End Type
            OasisRadioButtonGroup(
                label = "Ends",
                options = RepeatEndType.entries,
                selectedOption = repeatEndType,
                onOptionSelected = onRepeatEndTypeChange,
                optionToString = {
                    when (it) {
                        RepeatEndType.NEVER -> "Never"
                        RepeatEndType.ON_DATE -> "On date"
                        RepeatEndType.AFTER_OCCURRENCES -> "After number of times"
                    }
                }
            )

            // Conditional fields based on End Type selection
            when (repeatEndType) {
                RepeatEndType.ON_DATE -> {
                    OasisTextField(
                        state = repeatEndDateTextState,
                        label = "End Date",
                        readOnly = true,
                        errorText = repeatEndDateError,
                        interactionSource = endInteractionSource
                    )
                }
                RepeatEndType.AFTER_OCCURRENCES -> {
                    OasisTextField(
                        modifier = Modifier.fillMaxWidth(),
                        state = repeatEndOccurrencesTextState,
                        label = "Number of occurrences",
                        errorText = repeatEndOccurrencesError,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
                RepeatEndType.NEVER -> {}
            }
        }
    }
}


