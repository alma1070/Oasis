package com.almaslowcore.oasis.ui.components.buttons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.almaslowcore.oasis.ui.theme.AppTheme
import android.content.res.Configuration

@Composable
fun Fab(
    icon: ImageVector = Icons.Default.Add,
    contentDescription: String = "Floating action button",
    onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription)
    }
}

@Composable
fun SmallFab(
    icon: ImageVector = Icons.Default.Add,
    contentDescription: String = "Small floating action button",
    onClick: () -> Unit) {
    SmallFloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription)
    }
}

@Composable
fun LargeFab(
    icon: ImageVector = Icons.Default.Add,
    contentDescription: String = "Large floating action button",
    onClick: () -> Unit) {
    LargeFloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription)
    }
}

@Composable
fun ExtendedFab(
    text: String = "Extended FAB",
    icon: ImageVector = Icons.Default.Add,
    contentDescription: String = "Small floating action button",
    onClick: () -> Unit) {
    ExtendedFloatingActionButton(
        onClick = onClick,
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription
            )
        },
        text = {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge
            )
        },
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    )
}


@Preview(
    name = "All FAB Buttons",
    showBackground = true
)
@Composable
private fun AppFabCollectionPreview() {
    AppTheme {
        Surface {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Fab(
                    icon = Icons.Default.Add,
                    contentDescription = "Add",
                    onClick = {}
                )

                SmallFab(
                    icon = Icons.Default.Add,
                    contentDescription = "Add",
                    onClick = {}
                )

                LargeFab(
                    icon = Icons.Default.Add,
                    contentDescription = "Add",
                    onClick = {}
                )

                ExtendedFab(
                    text = "Edit",
                    icon = Icons.Default.Edit,
                    contentDescription = "Edit",
                    onClick = {}
                )
            }
        }
    }
}

@Preview(
    name = "All FAB Buttons - Dark Theme",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun AppFabCollectionDarkPreview() {
    AppTheme {
        Surface {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Fab(
                    icon = Icons.Default.Add,
                    contentDescription = "Add",
                    onClick = {}
                )

                SmallFab(
                    icon = Icons.Default.Add,
                    contentDescription = "Add",
                    onClick = {}
                )

                LargeFab(
                    icon = Icons.Default.Add,
                    contentDescription = "Add",
                    onClick = {}
                )

                ExtendedFab(
                    text = "Edit",
                    icon = Icons.Default.Edit,
                    contentDescription = "Edit",
                    onClick = {}
                )
            }
        }
    }
}
