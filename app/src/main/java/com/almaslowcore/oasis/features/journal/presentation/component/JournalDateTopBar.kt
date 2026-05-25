package com.almaslowcore.oasis.features.journal.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.almaslowcore.oasis.features.journal.presentation.state.JournalFilterMode
import com.almaslowcore.oasis.ui.components.topbar.OasisDateTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalDateTopBar(
    title: String,
    subtitle: String,
    selectedFilterMode: JournalFilterMode,
    isFilterMenuExpanded: Boolean,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    onDatePickerClick: () -> Unit,
    onFilterClick: () -> Unit,
    onDismissFilterMenu: () -> Unit,
    onFilterModeSelected: (JournalFilterMode) -> Unit,
    modifier: Modifier = Modifier
) {
    OasisDateTopBar(
        title = title,
        subtitle = subtitle,
        onPreviousClick = onPreviousClick,
        onNextClick = onNextClick,
        onTitleClick = onDatePickerClick, // Title click opens picker
        actions = {
            IconButton(onClick = onFilterClick) {
                Icon(Icons.Default.FilterList, "Filter")
            }
            // Keep your DropdownMenu here...
            DropdownMenu(
                expanded = isFilterMenuExpanded,
                onDismissRequest = onDismissFilterMenu
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    JournalFilterMenu(
                        selectedFilterMode = selectedFilterMode,
                        onFilterModeSelected = { mode ->
                            onFilterModeSelected(mode)
                            onDismissFilterMenu()
                        }
                    )
                }
            }
        }
    )
}