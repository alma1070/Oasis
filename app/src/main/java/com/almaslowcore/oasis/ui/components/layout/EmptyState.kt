package com.almaslowcore.oasis.ui.components.layout

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.almaslowcore.oasis.ui.components.buttons.FilledTonalButton
import com.almaslowcore.oasis.ui.components.buttons.TextButton
import com.almaslowcore.oasis.ui.theme.AppTheme

@Composable
fun EmptyState(
    title: String,
    modifier: Modifier = Modifier,
    message: String? = null,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    usePrimaryAction: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(vertical = 48.dp),
    illustration: (@Composable () -> Unit)? = null // whatever: icon, image,...
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(contentPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        illustration?.invoke()

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        message?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        if (actionText != null && onActionClick != null) {
            if (usePrimaryAction) {
                FilledTonalButton(
                    text = actionText,
                    onClick = onActionClick
                )
            } else {
                TextButton(
                    text = actionText,
                    onClick = onActionClick
                )
            }
        }
    }
}

@Preview(
    name = "EmptyState - Light",
    showBackground = true
)
@Composable
private fun EmptyStateLightPreview() {
    AppTheme(
        darkTheme = false,
        dynamicColor = false
    ) {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            EmptyState(
                modifier = Modifier.padding(16.dp),
                title = "No activities yet",
                message = "Create your first activity to start building your rhythm.",
                actionText = "Create activity",
                onActionClick = {},
                illustration = {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

            )
        }
    }
}

@Preview(
    name = "EmptyState - Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun EmptyStateDarkPreview() {
    AppTheme(
        darkTheme = true,
        dynamicColor = false
    ) {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            EmptyState(
                modifier = Modifier.padding(16.dp),
                title = "No activities yet",
                message = "Create your first activity to start building your rhythm.",
                actionText = "Create activity",
                onActionClick = {}
            )
        }
    }
}