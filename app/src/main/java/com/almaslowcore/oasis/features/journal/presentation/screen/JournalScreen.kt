package com.almaslowcore.oasis.features.journal.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.almaslowcore.oasis.features.journal.presentation.component.JournalDateTopBar
import com.almaslowcore.oasis.features.journal.presentation.state.JournalUiState
import com.almaslowcore.oasis.features.journal.presentation.viewModel.JournalViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.almaslowcore.oasis.R
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import com.almaslowcore.oasis.features.journal.presentation.component.DeleteJournalEntryDialog
import com.almaslowcore.oasis.features.journal.presentation.component.JournalDaySection
import com.almaslowcore.oasis.ui.components.buttons.ExtendedFab
import com.almaslowcore.oasis.ui.components.layout.OasisScreen

@Composable
fun JournalScreen(
    onNavigateToCreateJournal: () -> Unit,
    onNavigateToEditJournal: (entryId: Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: JournalViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    JournalScreenContent(
        uiState = uiState,
        onPreviousDateRange = viewModel::onPreviousDateRange,
        onNextDateRange = viewModel::onNextDateRange,
        onDateSelected = viewModel::onDateSelected,
        onFilterModeSelected = viewModel::onFilterModeSelected,
        onDeleteEntry = viewModel::onDeleteEntry,
        onNavigateToCreateJournal = onNavigateToCreateJournal,
        onNavigateToEditJournal = onNavigateToEditJournal,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun JournalScreenContent(
    uiState: JournalUiState,
    onPreviousDateRange: () -> Unit,
    onNextDateRange: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    onFilterModeSelected: (com.almaslowcore.oasis.features.journal.presentation.state.JournalFilterMode) -> Unit,
    onDeleteEntry: (Long) -> Unit,
    onNavigateToCreateJournal: () -> Unit,
    onNavigateToEditJournal: (entryId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var isFilterMenuExpanded by remember {
        mutableStateOf(false)
    }

    var isDatePickerVisible by remember {
        mutableStateOf(false)
    }

    var entryIdPendingDelete by remember {
        mutableStateOf<Long?>(null)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            JournalDateTopBar(
                title = uiState.title,
                subtitle = uiState.subtitle,
                selectedFilterMode = uiState.filterMode,
                isFilterMenuExpanded = isFilterMenuExpanded,
                onPreviousClick = onPreviousDateRange,
                onNextClick = onNextDateRange,
                onDatePickerClick = {
                    isDatePickerVisible = true
                },
                onFilterClick = {
                    isFilterMenuExpanded = true
                },
                onDismissFilterMenu = {
                    isFilterMenuExpanded = false
                },
                onFilterModeSelected = onFilterModeSelected
            )
        },
        floatingActionButton = {
            ExtendedFab(
                text = "Check in",
                icon = Icons.Filled.Add,
                contentDescription = stringResource(R.string.check_in),
                onClick = onNavigateToCreateJournal
            )
        }
    ) { innerPadding ->
        OasisScreen(
            modifier = Modifier.padding(innerPadding),
            scrollable = false, // Because JournalBody uses a LazyColumn
            contentPadding = PaddingValues(0.dp) // Let LazyColumn handle its own 16.dp padding
        ) {
            JournalBody(
                uiState = uiState,
                onCreateClick = onNavigateToCreateJournal,
                onEditEntry = onNavigateToEditJournal,
                onRequestDeleteEntry = { entryId ->
                    entryIdPendingDelete = entryId
                },
                modifier = Modifier.fillMaxSize()
            )
        }

    }

    if (isDatePickerVisible) {
        JournalDatePickerDialog(
            selectedDate = uiState.selectedDate,
            onDateSelected = { date ->
                onDateSelected(date)
                isDatePickerVisible = false
            },
            onDismiss = {
                isDatePickerVisible = false
            }
        )
    }

    entryIdPendingDelete?.let { entryId ->
        DeleteJournalEntryDialog(
            onConfirmDelete = {
                onDeleteEntry(entryId)
                entryIdPendingDelete = null
            },
            onDismiss = {
                entryIdPendingDelete = null
            }
        )
    }
}

@Composable
private fun JournalBody(
    uiState: JournalUiState,
    onEditEntry: (Long) -> Unit,
    onRequestDeleteEntry: (Long) -> Unit,
    onCreateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    when {
        uiState.isLoading -> {
            JournalLoadingState(modifier = modifier.fillMaxSize())
        }

        uiState.daySections.isEmpty() -> {
            JournalEmptyState(
                modifier = modifier.fillMaxSize()
            )
        }

        else -> {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                items(
                    items = uiState.daySections,
                    key = { section -> section.date.toString() }
                ) { section ->
                    JournalDaySection(
                        section = section,
                        onEditEntryClick = onEditEntry,
                        onDeleteEntryClick = onRequestDeleteEntry
                    )
                }
            }
        }
    }
}

@Composable
private fun JournalLoadingState(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun JournalEmptyState(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "No mood check-ins yet",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "Check in how you feel today.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun JournalErrorState(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun JournalDatePickerDialog(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate.toDatePickerMillis()
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val selectedDateMillis = datePickerState.selectedDateMillis

                    if (selectedDateMillis != null) {
                        onDateSelected(selectedDateMillis.toLocalDateFromDatePicker())
                    } else {
                        onDismiss()
                    }
                }
            ) {
                Text(text = "OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        }
    ) {
        DatePicker(
            state = datePickerState
        )
    }
}

@Composable
private fun DeleteJournalEntryDialog(
    onConfirmDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Delete this check-in?")
        },
        text = {
            Text(text = "This action cannot be undone.")
        },
        confirmButton = {
            TextButton(onClick = onConfirmDelete) {
                Text(text = "Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        }
    )
}

private fun LocalDate.toDatePickerMillis(): Long {
    return atStartOfDay(ZoneOffset.UTC)
        .toInstant()
        .toEpochMilli()
}

private fun Long.toLocalDateFromDatePicker(): LocalDate {
    return Instant
        .ofEpochMilli(this)
        .atZone(ZoneOffset.UTC)
        .toLocalDate()
}