package com.almaslowcore.oasis.features.journal.presentation.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun DeleteJournalEntryDialog(
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
            TextButton(
                onClick = onConfirmDelete
            ) {
                Text(text = "Delete")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(text = "Cancel")
            }
        }
    )
}