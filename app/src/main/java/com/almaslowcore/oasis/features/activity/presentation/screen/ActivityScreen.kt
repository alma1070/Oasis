package com.almaslowcore.oasis.features.activity.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.almaslowcore.oasis.R
import com.almaslowcore.oasis.features.activity.presentation.components.ActivityDatePickerDialog
import com.almaslowcore.oasis.features.activity.presentation.components.ActivityFilterSheet
import com.almaslowcore.oasis.features.activity.presentation.components.ActivityGroupByMenu
import com.almaslowcore.oasis.features.activity.presentation.components.ActivityListContent
import com.almaslowcore.oasis.features.activity.presentation.components.ActivityTopBar
import com.almaslowcore.oasis.features.activity.presentation.components.ActivityProgressDialog
import com.almaslowcore.oasis.features.activity.presentation.model.ActivityGroupBy
import com.almaslowcore.oasis.features.activity.presentation.model.ActivityPeriodMode
import com.almaslowcore.oasis.features.activity.presentation.model.ActivityScreenUiState
import com.almaslowcore.oasis.features.activity.presentation.model.ActivityTimeOfDayFilter
import com.almaslowcore.oasis.features.activity.presentation.viewModel.ActivityViewModel
import com.almaslowcore.oasis.features.journal.domain.model.MoodType
import com.almaslowcore.oasis.ui.components.buttons.Fab
import com.almaslowcore.oasis.ui.components.layout.OasisScreen
import java.time.LocalDate

@Composable
fun ActivityScreen(
    onNavigateToCreate: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ActivityViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ActivityScreenContent(
        uiState = uiState,
        onNavigateToCreate = onNavigateToCreate,
        onFilterClick = viewModel::openFilterSheet,
        onGroupByClick = viewModel::openGroupByMenu,
        onDatePickerClick = viewModel::openDatePicker,
        onActivityClick = viewModel::openProgressDialog,
        onPeriodModeChanged = viewModel::onPeriodModeChanged,
        onTimeOfDayFilterChanged = viewModel::onTimeOfDayFilterChanged,
        onCategoryCheckedChange = viewModel::onCategoryCheckedChange,
        onLifeAreaCheckedChange = viewModel::onLifeAreaCheckedChange,
        onClearAllFiltersClick = viewModel::clearAllFilters,
        onDismissFilterSheet = viewModel::dismissFilterSheet,
        onGroupBySelected = viewModel::onGroupByChanged,
        onDismissGroupByMenu = viewModel::dismissGroupByMenu,
        onPreviousClick = viewModel::onPreviousDate, // NEW
        onNextClick = viewModel::onNextDate,
        onDateSelected = viewModel::onDateSelected,
        onDismissDatePicker = viewModel::dismissDatePicker,
        onDismissProgressDialog = viewModel::dismissProgressDialog,
        onCompleteYesNoActivity = viewModel::completeYesNoActivity,
        onSaveNumericProgress = viewModel::saveNumericProgress,
        onSaveChecklistProgress = viewModel::saveChecklistProgress,
        modifier = modifier
    )
}

@Composable
private fun ActivityScreenContent(
    uiState: ActivityScreenUiState,

    onNavigateToCreate: () -> Unit,
    onPreviousClick: () -> Unit, // NEW
    onNextClick: () -> Unit,
    onFilterClick: () -> Unit,
    onGroupByClick: () -> Unit,
    onDatePickerClick: () -> Unit,
    onActivityClick: (String) -> Unit,

    onPeriodModeChanged: (ActivityPeriodMode) -> Unit,
    onTimeOfDayFilterChanged: (ActivityTimeOfDayFilter) -> Unit,
    onCategoryCheckedChange: (String, Boolean) -> Unit,
    onLifeAreaCheckedChange: (String, Boolean) -> Unit,
    onClearAllFiltersClick: () -> Unit,
    onDismissFilterSheet: () -> Unit,

    onGroupBySelected: (ActivityGroupBy) -> Unit,
    onDismissGroupByMenu: () -> Unit,

    onDateSelected: (LocalDate) -> Unit,
    onDismissDatePicker: () -> Unit,

    onDismissProgressDialog: () -> Unit,
    onCompleteYesNoActivity: (String, String, MoodType?) -> Unit,
    onSaveNumericProgress: (String, Double, String, MoodType?) -> Unit,
    onSaveChecklistProgress: (String, Set<String>, String, MoodType?) -> Unit,

    modifier: Modifier = Modifier
) {
    // In ActivityScreen.kt
    Scaffold(
        topBar = {
            ActivityTopBar(
                title = uiState.topBar.title,
                subtitle = uiState.topBar.subtitle,
                onPreviousClick = onPreviousClick, // Pass it here
                onNextClick = onNextClick,
                onFilterClick = onFilterClick,
                onGroupByClick = onGroupByClick,
                onDatePickerClick = onDatePickerClick
            ) },
        floatingActionButton = {
            Fab(
                icon = Icons.Filled.Add,
                contentDescription = stringResource(R.string.check_in),
                onClick = onNavigateToCreate
            )
        }
    ) { innerPadding ->
        OasisScreen(
            modifier = Modifier.padding(innerPadding),
            scrollable = false, // Because JournalBody uses a LazyColumn
            contentPadding = PaddingValues(0.dp)
        ) {
            when {
                uiState.isLoading -> ActivityLoadingContent(Modifier.fillMaxSize())
                uiState.errorMessage != null -> ActivityErrorContent(uiState.errorMessage, Modifier.fillMaxSize())
                else -> ActivityListContent(
                    sections = uiState.sections,
                    groupBy = uiState.filterState.groupBy,
                    onActivityClick = onActivityClick,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    if (uiState.isFilterSheetOpen) {
        ActivityFilterSheet(
            filterState = uiState.filterState,
            availableCategoryIds = uiState.availableCategoryIds,
            availableLifeAreaIds = uiState.availableLifeAreaIds,
            onPeriodModeChanged = onPeriodModeChanged,
            onTimeOfDayFilterChanged = onTimeOfDayFilterChanged,
            onCategoryCheckedChange = onCategoryCheckedChange,
            onLifeAreaCheckedChange = onLifeAreaCheckedChange,
            onClearAllClick = onClearAllFiltersClick,
            onDismiss = onDismissFilterSheet
        )
    }

    if (uiState.isDatePickerOpen) {
        ActivityDatePickerDialog(
            selectedDate = uiState.filterState.selectedDate,
            onDateSelected = onDateSelected,
            onDismiss = onDismissDatePicker
        )
    }

    val selectedActivity = uiState.activities.firstOrNull { activity ->
        activity.id == uiState.selectedActivityId
    }

    selectedActivity?.let { activity ->
        ActivityProgressDialog(
            activity = activity,
            onDismiss = onDismissProgressDialog,
            onCompleteYesNo = onCompleteYesNoActivity,
            onSaveNumeric = onSaveNumericProgress,
            onSaveChecklist = onSaveChecklistProgress
        )
    }
}

@Composable
private fun ActivityLoadingContent(
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
private fun ActivityErrorContent(
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