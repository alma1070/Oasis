package com.almaslowcore.oasis.features.activity.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.almaslowcore.oasis.features.activity.presentation.model.ActivityFilterState
import com.almaslowcore.oasis.features.activity.presentation.model.ActivityPeriodMode
import com.almaslowcore.oasis.features.activity.presentation.model.ActivityTimeOfDayFilter
import com.almaslowcore.oasis.ui.components.OasisFilterChipGroup

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class
)
@Composable
fun ActivityFilterSheet(
    filterState: ActivityFilterState,
    availableCategoryIds: List<String>,
    availableLifeAreaIds: List<String>,
    onPeriodModeChanged: (ActivityPeriodMode) -> Unit,
    onTimeOfDayFilterChanged: (ActivityTimeOfDayFilter) -> Unit,
    onCategoryCheckedChange: (String, Boolean) -> Unit,
    onLifeAreaCheckedChange: (String, Boolean) -> Unit,
    onClearAllClick: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
        ) {
            ActivityFilterSheetHeader(
                onClearAllClick = onClearAllClick,
                onDismiss = onDismiss
            )

            Spacer(modifier = Modifier.height(16.dp))

            OasisFilterChipGroup(
                label = "Period",
                options = ActivityPeriodMode.entries,
                selectedOption = filterState.periodMode,
                onOptionSelected = onPeriodModeChanged,
                optionToString = { periodMode ->
                    periodMode.toDisplayText()
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            OasisFilterChipGroup(
                label = "Time of day",
                options = ActivityTimeOfDayFilter.entries,
                selectedOption = filterState.timeOfDayFilter,
                onOptionSelected = onTimeOfDayFilterChanged,
                optionToString = { timeOfDayFilter ->
                    timeOfDayFilter.toDisplayText()
                }
            )

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(20.dp))

            CheckboxFilterSection(
                title = "Category",
                items = availableCategoryIds,
                selectedIds = filterState.selectedCategoryIds,
                emptyText = "No categories yet",
                allSelectedText = "All categories",
                onCheckedChange = onCategoryCheckedChange
            )

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(20.dp))

            CheckboxFilterSection(
                title = "Life Area",
                items = availableLifeAreaIds,
                selectedIds = filterState.selectedLifeAreaIds,
                emptyText = "No life areas yet",
                allSelectedText = "All life areas",
                onCheckedChange = onLifeAreaCheckedChange
            )
        }
    }
}

@Composable
private fun ActivityFilterSheetHeader(
    onClearAllClick: () -> Unit,
    onDismiss: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Filter activities",
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "Choose what should appear in the list",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        TextButton(
            onClick = onClearAllClick
        ) {
            Text(text = "Clear")
        }

        TextButton(
            onClick = onDismiss
        ) {
            Text(text = "Done")
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun <T> SingleSelectionChipRow(
    options: List<T>,
    selectedOption: T,
    label: (T) -> String,
    onOptionSelected: (T) -> Unit
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { option ->
            FilterChip(
                selected = option == selectedOption,
                onClick = {
                    onOptionSelected(option)
                },
                label = {
                    Text(
                        text = label(option)
                    )
                }
            )
        }
    }
}

@Composable
private fun CheckboxFilterSection(
    title: String,
    emptyText: String,
    items: List<String>,
    selectedItems: Set<String>,
    allSelectedText: String,
    onCheckedChange: (String, Boolean) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        FilterSectionTitle(
            title = title
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (items.isEmpty()) {
            Text(
                text = emptyText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            return
        }

        if (selectedItems.isEmpty()) {
            Text(
                text = allSelectedText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        items.forEach { item ->
            CheckboxFilterRow(
                text = item,
                checked = selectedItems.isEmpty() || item in selectedItems,
                onCheckedChange = { checked ->
                    onCheckedChange(item, checked)
                }
            )
        }
    }
}

@Composable
private fun CheckboxFilterRow(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun FilterSectionTitle(
    title: String
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium
    )
}

private fun ActivityPeriodMode.toDisplayText(): String {
    return when (this) {
        ActivityPeriodMode.DAY -> "Day"
        ActivityPeriodMode.WEEK -> "Week"
        ActivityPeriodMode.MONTH -> "Month"
    }
}

private fun ActivityTimeOfDayFilter.toDisplayText(): String {
    return when (this) {
        ActivityTimeOfDayFilter.ANYTIME -> "Anytime"
        ActivityTimeOfDayFilter.START_OF_DAY -> "Start of Day"
        ActivityTimeOfDayFilter.AFTERNOON -> "Afternoon"
        ActivityTimeOfDayFilter.EVENING -> "Evening"
        ActivityTimeOfDayFilter.BEDTIME -> "Bedtime"
    }
}