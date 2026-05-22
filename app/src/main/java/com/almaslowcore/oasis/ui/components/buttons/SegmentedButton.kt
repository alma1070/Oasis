package com.almaslowcore.oasis.ui.components.buttons

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.almaslowcore.oasis.ui.theme.AppTheme

@Composable
fun SingleChoiceSegmentedButton(
    options: List<String>,
    selectedIndex: Int,
    onOptionSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    SingleChoiceSegmentedButtonRow(
        modifier = modifier
    ) {
        options.forEachIndexed { index, label ->
            SegmentedButton(
                selected = selectedIndex == index,
                onClick = {
                    onOptionSelected(index)
                },
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = options.size
                ),
                icon = {
                    if (selectedIndex == index) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null
                        )
                    }
                }
            ) {
                Text(text = label)
            }
        }
    }
}

@Composable
private fun AppSegmentedButtonPreviewContent() {
    var selectedIndex by remember {
        mutableIntStateOf(0)
    }

    Surface {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Task Filter",
                style = MaterialTheme.typography.titleMedium
            )

            SingleChoiceSegmentedButton(
                options = listOf("All", "Active", "Done"),
                selectedIndex = selectedIndex,
                onOptionSelected = {
                    selectedIndex = it
                }
            )

            Text(
                text = "Selected: ${listOf("All", "Active", "Done")[selectedIndex]}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview(
    name = "Segmented Button - Light Theme",
    showBackground = true
)
@Composable
private fun AppSegmentedButtonLightPreview() {
    AppTheme {
        AppSegmentedButtonPreviewContent()
    }
}

@Preview(
    name = "Segmented Button - Dark Theme",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun AppSegmentedButtonDarkPreview() {
    AppTheme {
        AppSegmentedButtonPreviewContent()
    }
}