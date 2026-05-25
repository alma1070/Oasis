package com.almaslowcore.oasis.ui.components.buttons

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Composable
fun <T> OasisRadioButtonGroup(
    label: String? = null,
    options: List<T>,
    selectedOption: T?,
    onOptionSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    optionToString: @Composable (T) -> String = { it.toString() }
) {
    Column(
        modifier = modifier.selectableGroup()) {
        // Optional Section Label
        if (label != null) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        options.forEach { option ->
            val isSelected = (option == selectedOption)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp) // Minimum touch target height
                    .selectable(
                        selected = isSelected,
                        onClick = { onOptionSelected(option) },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = isSelected,
                    onClick = null // Selected by the Row's clickable/selectable modifier
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = optionToString(option),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}