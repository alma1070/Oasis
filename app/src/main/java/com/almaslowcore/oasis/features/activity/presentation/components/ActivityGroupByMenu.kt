package com.almaslowcore.oasis.features.activity.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.almaslowcore.oasis.R
import com.almaslowcore.oasis.features.activity.presentation.model.ActivityGroupBy

@Composable
fun ActivityGroupByMenu(
    expanded: Boolean,
    selectedGroupBy: ActivityGroupBy,
    onGroupBySelected: (ActivityGroupBy) -> Unit,
    onDismiss: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss
    ) {
        ActivityGroupBy.entries.forEach { groupBy ->
            DropdownMenuItem(
                text = {
                    Text(
                        text = groupBy.toDisplayText()
                    )
                },
                leadingIcon = {
                    if (groupBy == selectedGroupBy) {
                        Icon(
                            imageVector = Icons.Outlined.Check,
                            contentDescription = null
                        )
                    }
                },
                onClick = {
                    onGroupBySelected(groupBy)
                    onDismiss()
                }
            )
        }
    }
}

@Composable
private fun ActivityGroupBy.toDisplayText(): String {
    return when (this) {
        ActivityGroupBy.NONE -> stringResource(R.string.none)
        ActivityGroupBy.TIME_OF_DAY -> stringResource(R.string.time_of_day)
        ActivityGroupBy.CATEGORY -> stringResource(R.string.category)
        ActivityGroupBy.LIFE_AREA -> stringResource(R.string.life_area)
    }
}