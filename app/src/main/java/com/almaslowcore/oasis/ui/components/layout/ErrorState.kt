package com.almaslowcore.oasis.ui.components.layout

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
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
import com.almaslowcore.oasis.ui.components.buttons.OutlinedButton
import com.almaslowcore.oasis.ui.components.buttons.TextButton
import com.almaslowcore.oasis.ui.theme.AppTheme

@Composable
fun ErrorState(
    modifier: Modifier = Modifier,
    title: String = "Something went wrong",
    message: String? = null,
    actionText: String? = "Try again",
    onActionClick: (() -> Unit)? = null,
    usePrimaryAction: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(vertical = 48.dp),
    illustration: (@Composable () -> Unit)? = null
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
            color = MaterialTheme.colorScheme.error,
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
                OutlinedButton(
                    actionText,
                    onActionClick
                )
            } else {
                TextButton(
                    actionText,
                    onActionClick
                )
            }
        }
    }
}

@Preview(
    name = "ErrorState - Light",
    showBackground = true
)
@Composable
private fun ErrorStateLightPreview() {
    AppTheme(
        darkTheme = false,
        dynamicColor = false
    ) {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            ErrorState(
                modifier = Modifier.padding(16.dp),
                title = "Unable to load activities",
                message = "Something interrupted your data. Please try again.",
                actionText = "Try again",
                onActionClick = {}
            )
        }
    }
}

@Preview(
    name = "ErrorState - Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun ErrorStateDarkPreview() {
    AppTheme(
        darkTheme = true,
        dynamicColor = false
    ) {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            ErrorState(
                modifier = Modifier.padding(16.dp),
                title = "Unable to load activities",
                message = "Something interrupted your data. Please try again.",
                actionText = "Try again",
                onActionClick = {},
                illustration = {
                    Icon(
                        imageVector = Icons.Outlined.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            )
        }
    }
}