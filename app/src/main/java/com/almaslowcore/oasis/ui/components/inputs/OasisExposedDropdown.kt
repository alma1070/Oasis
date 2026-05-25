package com.almaslowcore.oasis.ui.components.inputs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> OasisDropdown(
    label: String,
    options: List<T>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    errorText: String? = null,
    optionToString: (T) -> String = { it.toString() } // Custom mapping for complex objects
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        // This is the container that links the TextField and the Menu
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = modifier
        ) {
            OutlinedTextField(
                // The menuAnchor modifier is critical for positioning
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                readOnly = true,
                value = optionToString(selectedOption),
                onValueChange = {},
                label = { Text(label) },
                isError = errorText != null,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = optionToString(option),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    )
                }
            }
        }
        if (errorText != null) {
            Text(
                text = errorText,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

/*
* Example 1: Simple Strings
* val categories = listOf("Food", "Rent", "Utilities", "Entertainment")
* var selectedCategory by remember { mutableStateOf(categories[0]) }
* OasisDropdown(
* label = "Select Category",
* options = categories,
* selectedOption = selectedCategory,
* onOptionSelected = { selectedCategory = it }
)
 */

/*
Example 2: Complex Objects (e.g., Room Entities)
data class User(val id: Int, val name: String)

val users = listOf(User(1, "Alice"), User(2, "Bob"))
var selectedUser by remember { mutableStateOf(users[0]) }

OasisDropdown(
    label = "Assign User",
    options = users,
    selectedOption = selectedUser,
    onOptionSelected = { selectedUser = it },
    optionToString = { it.name } // Tells the component to display the name
)
 */