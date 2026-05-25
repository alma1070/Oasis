package com.almaslowcore.oasis.features.activity.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.almaslowcore.oasis.R
import com.almaslowcore.oasis.features.activity.presentation.model.ActivityUiMeasurableMode
import com.almaslowcore.oasis.features.activity.presentation.model.ActivityUiModel
import com.almaslowcore.oasis.features.activity.presentation.model.ActivityUiTrackingType

@Composable
fun ActivityProgressDialog(
    activity: ActivityUiModel,
    onDismiss: () -> Unit,
    onCompleteYesNo: (activityId: String, note: String) -> Unit,
    onSaveNumeric: (activityId: String, value: Double, note: String) -> Unit,
    onSaveChecklist: (activityId: String, completedSubtaskIds: Set<String>, note: String) -> Unit
) {
    when (activity.trackingType) {
        ActivityUiTrackingType.YES_NO -> {
            YesNoProgressDialog(
                activity = activity,
                onDismiss = onDismiss,
                onComplete = onCompleteYesNo
            )
        }
        ActivityUiTrackingType.MEASURABLE if activity.measurableMode == ActivityUiMeasurableMode.NUMERIC -> {
            NumericProgressDialog(
                activity = activity,
                onDismiss = onDismiss,
                onSave = onSaveNumeric
            )
        }
        ActivityUiTrackingType.MEASURABLE if activity.measurableMode == ActivityUiMeasurableMode.CHECKLIST -> {
            ChecklistProgressDialog(
                activity = activity,
                onDismiss = onDismiss,
                onSave = onSaveChecklist
            )
        }
        else -> {
            UnsupportedProgressDialog(
                activity = activity,
                onDismiss = onDismiss
            )
        }
    }
}

@Composable
private fun YesNoProgressDialog(
    activity: ActivityUiModel,
    onDismiss: () -> Unit,
    onComplete: (activityId: String, note: String) -> Unit
) {
    var note by remember(activity.id) {
        mutableStateOf("")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = activity.title)
        },
        text = {
            OutlinedTextField(
                value = note,
                onValueChange = {
                    note = it
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(text = stringResource(R.string.note))
                },
                minLines = 3,
                maxLines = 5
            )
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(text = stringResource(R.string.cancel))
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onComplete(
                        activity.id,
                        note.trim()
                    )
                }
            ) {
                Text(text = stringResource(R.string.complete))
            }
        }
    )
}

@Composable
private fun NumericProgressDialog(
    activity: ActivityUiModel,
    onDismiss: () -> Unit,
    onSave: (activityId: String, value: Double, note: String) -> Unit
) {
    var valueText by remember(activity.id) {
        mutableStateOf(
            activity.currentValue
                ?.toCleanText()
                .orEmpty()
        )
    }

    var note by remember(activity.id) {
        mutableStateOf("")
    }

    val value = valueText.toProgressDoubleOrNull()

    val isValueInvalid = valueText.isNotBlank() && value == null

    val canSave = value != null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = activity.title)
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = valueText,
                    onValueChange = {
                        valueText = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(text = stringResource(R.string.progress))
                    },
                    suffix = {
                        activity.unit
                            ?.takeIf { it.isNotBlank() }
                            ?.let {
                                Text(text = it)
                            }
                    },
                    singleLine = true,
                    isError = isValueInvalid,
                    supportingText = {
                        NumericSupportingText(
                            isValueInvalid = isValueInvalid,
                            targetValue = activity.targetValue,
                            unit = activity.unit
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    )
                )

                OutlinedTextField(
                    value = note,
                    onValueChange = {
                        note = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(text = stringResource(R.string.note))
                    },
                    minLines = 3,
                    maxLines = 5
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(text = stringResource(R.string.cancel))
            }
        },
        confirmButton = {
            Button(
                enabled = canSave,
                onClick = {
                    val progressValue = value ?: return@Button

                    onSave(
                        activity.id,
                        progressValue,
                        note.trim()
                    )
                }
            ) {
                Text(text = stringResource(R.string.save))
            }
        }
    )
}

@Composable
private fun NumericSupportingText(
    isValueInvalid: Boolean,
    targetValue: Double?,
    unit: String?
) {
    when {
        isValueInvalid -> {
            Text(text = stringResource(R.string.enterValidNum))
        }

        targetValue != null && targetValue > 0.0 -> {
            Text(
                text = "${stringResource(R.string.target)}: ${targetValue.toCleanText()} ${unit.orEmpty()}"
            )
        }

        else -> {
            Text(text = stringResource(R.string.noTarget))
        }
    }
}

@Composable
private fun ChecklistProgressDialog(
    activity: ActivityUiModel,
    onDismiss: () -> Unit,
    onSave: (activityId: String, completedSubtaskIds: Set<String>, note: String) -> Unit
) {
    var checkedSubtaskIds by remember(activity.id) {
        mutableStateOf(
            activity.subtasks
                .filter { it.isCompleted }
                .map { it.id }
                .toSet()
        )
    }

    var note by remember(activity.id) {
        mutableStateOf("")
    }

    val sortedSubtasks = remember(activity.id, activity.subtasks) {
        activity.subtasks.sortedBy { it.orderIndex }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = activity.title)
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (sortedSubtasks.isEmpty()) {
                    Text(
                        text = stringResource(R.string.noSubtasks),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.heightIn(
                            max = 260.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(
                            items = sortedSubtasks,
                            key = { it.id }
                        ) { subtask ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Checkbox(
                                    checked = subtask.id in checkedSubtaskIds,
                                    onCheckedChange = { checked ->
                                        checkedSubtaskIds = if (checked) {
                                            checkedSubtaskIds + subtask.id
                                        } else {
                                            checkedSubtaskIds - subtask.id
                                        }
                                    }
                                )

                                Text(
                                    text = subtask.title,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = note,
                    onValueChange = {
                        note = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(text = "Ghi chú")
                    },
                    minLines = 3,
                    maxLines = 5
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(text = stringResource(R.string.cancel))
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        activity.id,
                        checkedSubtaskIds,
                        note.trim()
                    )
                }
            ) {
                Text(text = stringResource(R.string.save))
            }
        }
    )
}

@Composable
private fun UnsupportedProgressDialog(
    activity: ActivityUiModel,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = activity.title)
        },
        text = {
            Text(text = stringResource(R.string.unsupportedAcivityType))
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(text = stringResource(R.string.close))
            }
        }
    )
}

private fun String.toProgressDoubleOrNull(): Double? {
    return trim()
        .replace(",", ".")
        .toDoubleOrNull()
}

private fun Double.toCleanText(): String {
    return if (this % 1.0 == 0.0) {
        this.toInt().toString()
    } else {
        this.toString()
    }
}