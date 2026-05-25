package com.almaslowcore.oasis.features.activity.presentation.components

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.almaslowcore.oasis.R
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityDatePickerDialog(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate.toUtcMillis()
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val selectedDateMillis = datePickerState.selectedDateMillis

                    if (selectedDateMillis != null) {
                        onDateSelected(
                            selectedDateMillis.toLocalDateFromUtcMillis()
                        )
                    }

                    onDismiss()
                }
            ) {
                Text(text = stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(text = stringResource(R.string.cancel))
            }
        }
    ) {
        DatePicker(
            state = datePickerState
        )
    }
}

private fun LocalDate.toUtcMillis(): Long {
    return atStartOfDay()
        .toInstant(ZoneOffset.UTC)
        .toEpochMilli()
}

private fun Long.toLocalDateFromUtcMillis(): LocalDate {
    return Instant.ofEpochMilli(this)
        .atZone(ZoneOffset.UTC)
        .toLocalDate()
}