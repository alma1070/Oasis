package com.almaslowcore.oasis.features.journal.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AssistChip
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.almaslowcore.oasis.features.journal.presentation.state.ActivitySummaryUiState

@Composable
fun RelatedActivityDropdown(
    selectedActivityId: Long?,
    availableActivities: List<ActivitySummaryUiState>,
    onActivitySelected: (Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    val selectedActivityTitle = availableActivities
        .firstOrNull { activity ->
            activity.id == selectedActivityId
        }
        ?.title

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        JournalSectionTitle(text = "Related activity")

        androidx.compose.foundation.layout.Box {
            AssistChip(
                onClick = {
                    expanded = true
                },
                label = {
                    Text(
                        text = selectedActivityTitle ?: "No activity"
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = null
                    )
                }
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                }
            ) {
                DropdownMenuItem(
                    text = {
                        Text(text = "No activity")
                    },
                    onClick = {
                        onActivitySelected(null)
                        expanded = false
                    }
                )

                if (availableActivities.isEmpty()) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "No activities for this day",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        enabled = false,
                        onClick = {}
                    )
                } else {
                    availableActivities.forEach { activity ->
                        DropdownMenuItem(
                            text = {
                                Text(text = activity.title)
                            },
                            onClick = {
                                onActivitySelected(activity.id)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}