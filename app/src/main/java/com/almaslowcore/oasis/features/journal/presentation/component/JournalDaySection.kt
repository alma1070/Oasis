package com.almaslowcore.oasis.features.journal.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.almaslowcore.oasis.features.journal.presentation.state.JournalDaySectionUiState

@Composable
fun JournalDaySection(
    section: JournalDaySectionUiState,
    onEditEntryClick: (entryId: Long) -> Unit,
    onDeleteEntryClick: (entryId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = section.title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        section.entries.forEach { entry ->
            JournalMoodCard(
                entry = entry,
                onEditClick = {
                    onEditEntryClick(entry.id)
                },
                onDeleteClick = {
                    onDeleteEntryClick(entry.id)
                }
            )
        }
    }
}