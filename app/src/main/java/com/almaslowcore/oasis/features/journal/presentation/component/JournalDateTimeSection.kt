package com.almaslowcore.oasis.features.journal.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalDateTimeSection(
    selectedDate: LocalDate,
    selectedTime: LocalTime,
    onDateSelected: (LocalDate) -> Unit,
    onTimeSelected: (LocalTime) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val dateFormatter = remember {
        DateTimeFormatter.ofPattern("EEE, d MMM yyyy", Locale.getDefault())
    }

    val timeFormatter = remember {
        DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        JournalSectionTitle(text = "When")

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AssistChip(
                onClick = { showDatePicker = true },
                label = {
                    Text(text = selectedDate.format(dateFormatter))
                }
            )

            AssistChip(
                onClick = { showTimePicker = true },
                label = {
                    Text(text = selectedTime.format(timeFormatter))
                }
            )
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate.toDatePickerMillis()
        )

        DatePickerDialog(
            onDismissRequest = {
                showDatePicker = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedDateMillis = datePickerState.selectedDateMillis

                        if (selectedDateMillis != null) {
                            onDateSelected(selectedDateMillis.toLocalDateFromDatePicker())
                        }

                        showDatePicker = false
                    }
                ) {
                    Text(text = "OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                    }
                ) {
                    Text(text = "Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = selectedTime.hour,
            initialMinute = selectedTime.minute,
            is24Hour = true
        )

        AlertDialog(
            onDismissRequest = {
                showTimePicker = false
            },
            title = {
                Text(text = "Choose time")
            },
            text = {
                TimePicker(state = timePickerState)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onTimeSelected(
                            LocalTime.of(
                                timePickerState.hour,
                                timePickerState.minute
                            )
                        )

                        showTimePicker = false
                    }
                ) {
                    Text(text = "OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showTimePicker = false
                    }
                ) {
                    Text(text = "Cancel")
                }
            }
        )
    }
}

@Composable
fun JournalSectionTitle(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface
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