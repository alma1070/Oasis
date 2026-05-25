package com.almaslowcore.oasis.features.journal.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.almaslowcore.oasis.features.journal.presentation.state.JournalFilterMode

@Composable
fun JournalFilterMenu(
    selectedFilterMode: JournalFilterMode,
    onFilterModeSelected: (JournalFilterMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        JournalFilterMode.entries.forEach { mode ->
            FilterChip(
                selected = selectedFilterMode == mode,
                onClick = {
                    onFilterModeSelected(mode)
                },
                label = {
                    Text(text = mode.toLabel())
                }
            )
        }
    }
}

private fun JournalFilterMode.toLabel(): String {
    return when (this) {
        JournalFilterMode.DAY -> "Day"
        JournalFilterMode.WEEK -> "Week"
        JournalFilterMode.MONTH -> "Month"
    }
}