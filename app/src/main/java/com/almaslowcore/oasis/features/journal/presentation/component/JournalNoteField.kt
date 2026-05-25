package com.almaslowcore.oasis.features.journal.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun JournalNoteField(
    note: String,
    noteError: String?,
    onNoteChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        JournalSectionTitle(text = "Note")

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = note,
            onValueChange = onNoteChange,
            minLines = 5,
            maxLines = Int.MAX_VALUE,
            label = {
                Text(text = "What happened?")
            },
            placeholder = {
                Text(text = "Write anything you want to remember...")
            },
            isError = noteError != null,
            supportingText = {
                if (noteError != null) {
                    Text(
                        text = noteError,
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    Text(text = "Optional")
                }
            }
        )
    }
}