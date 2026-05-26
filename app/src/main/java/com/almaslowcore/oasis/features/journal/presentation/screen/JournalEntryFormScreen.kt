package com.almaslowcore.oasis.features.journal.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.almaslowcore.oasis.features.journal.domain.model.MoodType
import com.almaslowcore.oasis.features.journal.presentation.state.JournalEntryFormMode
import com.almaslowcore.oasis.features.journal.presentation.state.JournalFormState
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.ui.platform.LocalLocale
import com.almaslowcore.oasis.features.journal.presentation.component.JournalDateTimeSection
import com.almaslowcore.oasis.features.journal.presentation.component.JournalNoteField
import com.almaslowcore.oasis.features.journal.presentation.component.MoodSelectorRow
import java.time.LocalDate
import java.time.LocalTime
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.almaslowcore.oasis.R
import com.almaslowcore.oasis.features.journal.presentation.state.JournalEntryFormEvent
import com.almaslowcore.oasis.features.journal.presentation.viewModel.JournalEntryFormViewModel
import com.almaslowcore.oasis.ui.components.layout.OasisScreen
import com.almaslowcore.oasis.ui.navigation.BottomBarAction
import com.almaslowcore.oasis.ui.navigation.BottomBarConfig
import com.almaslowcore.oasis.ui.navigation.LocalBottomBarController

@Composable
fun JournalEntryFormScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: JournalEntryFormViewModel = hiltViewModel()
) {
    val formState by viewModel.formState.collectAsStateWithLifecycle()
    val bottomBarController = LocalBottomBarController.current // Get global controller

    // Unified Style: Use DisposableEffect to configure the Global Bottom Bar
    DisposableEffect(formState.isSaving, viewModel.mode) {
        val saveText = when (viewModel.mode) {
            JournalEntryFormMode.Create -> "Save"
            is JournalEntryFormMode.Edit -> "Update"
        }

        bottomBarController.updateConfig(
            BottomBarConfig(
                actions = listOf(
                    BottomBarAction(
                        label = "Cancel",
                        onClick = onNavigateBack
                    ),
                    BottomBarAction(
                        label = if (formState.isSaving) "Saving..." else saveText,
                        icon = Icons.Default.Done, // Align with CreateActivity style
                        enabled = !formState.isSaving,
                        isPrimary = true,
                        onClick = viewModel::onSaveClick
                    )
                )
            )
        )
        onDispose { bottomBarController.clear() }
    }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            if (event is JournalEntryFormEvent.NavigateBack) onNavigateBack()
        }
    }

    // Now this is just a clean content wrapper
    JournalEntryFormContent(
        mode = viewModel.mode,
        formState = formState,
        onDateSelected = viewModel::onDateChange,
        onTimeSelected = viewModel::onTimeChange,
        onMoodSelected = viewModel::onMoodSelected,
        onNoteChange = viewModel::onNoteChange,
        onCancelClick = viewModel::onCancelClick,
        onSaveClick = viewModel::onSaveClick,
        modifier = modifier
    )
}



@Composable
private fun JournalEntryFormContent(
    mode: JournalEntryFormMode,
    formState: JournalFormState,
    onDateSelected: (LocalDate) -> Unit,
    onTimeSelected: (LocalTime) -> Unit,
    onMoodSelected: (MoodType) -> Unit,
    onNoteChange: (String) -> Unit,
    onCancelClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val saveText = when (mode) {
        JournalEntryFormMode.Create -> stringResource(R.string.save)
        is JournalEntryFormMode.Edit -> stringResource(R.string.update)
    }

    OasisScreen(
        modifier = Modifier
            .imePadding(), // Handles keyboard appearance
        scrollable = true,
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        JournalDateTimeSection(
            selectedDate = formState.selectedDate,
            selectedTime = formState.selectedTime,
            onDateSelected = onDateSelected,
            onTimeSelected = onTimeSelected
        )

        MoodSelectorRow(
            selectedMood = formState.selectedMood,
            onMoodSelected = onMoodSelected
        )

        JournalNoteField(
            note = formState.note,
            noteError = formState.noteError,
            onNoteChange = onNoteChange
        )
    }
}

@Composable
private fun DateTimeSection(
    formState: JournalFormState,
    onDateClick: () -> Unit,
    onTimeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormatter = DateTimeFormatter.ofPattern(
        "EEE, d MMM yyyy",
        LocalLocale.current.platformLocale
    )

    val timeFormatter = DateTimeFormatter.ofPattern(
        "HH:mm",
        LocalLocale.current.platformLocale
    )

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SectionTitle(text = "When")

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AssistChip(
                onClick = onDateClick,
                label = {
                    Text(
                        text = formState.selectedDate.format(dateFormatter)
                    )
                }
            )

            AssistChip(
                onClick = onTimeClick,
                label = {
                    Text(
                        text = formState.selectedTime.format(timeFormatter)
                    )
                }
            )
        }
    }
}

@Composable
private fun MoodSection(
    selectedMood: MoodType,
    onMoodSelected: (MoodType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SectionTitle(text = "Mood")

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MoodType.entries.forEach { mood ->
                FilterChip(
                    selected = selectedMood == mood,
                    onClick = {
                        onMoodSelected(mood)
                    },
                    label = {
                        Text(text = "${mood.emoji} ${mood.defaultLabel}")
                    }
                )
            }
        }
    }
}


@Composable
private fun NoteSection(
    note: String,
    noteError: String?,
    onNoteChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SectionTitle(text = "Note")

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = note,
            onValueChange = onNoteChange,
            minLines = 5,
            label = {
                Text(text = "What happened?")
            },
            isError = noteError != null,
            supportingText = {
                if (noteError != null) {
                    Text(text = noteError)
                }
            }
        )
    }
}

@Composable
private fun SectionTitle(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun JournalFormBottomBar(
    saveText: String,
    isSaving: Boolean,
    onCancelClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TextButton(
            modifier = Modifier.weight(1f),
            onClick = onCancelClick,
            enabled = !isSaving
        ) {
            Text(text = "Cancel")
        }

        Button(
            modifier = Modifier.weight(1f),
            onClick = onSaveClick,
            enabled = !isSaving
        ) {
            Text(
                text = if (isSaving) "Saving..." else saveText
            )
        }
    }
}