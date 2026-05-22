package com.almaslowcore.oasis.ui.components.buttons


import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.almaslowcore.oasis.ui.theme.AppTheme

@Composable
fun FilledButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 24.dp,
        vertical = 12.dp
    )
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        contentPadding = contentPadding
    ) {
        Text(text = text)
    }
}

@Composable
fun FilledTonalButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 24.dp,
        vertical = 12.dp
    )
) {
    FilledTonalButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        contentPadding = contentPadding
    ) {
        Text(text = text)
    }
}

@Composable
fun ElevatedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 24.dp,
        vertical = 12.dp
    )
) {
    ElevatedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        contentPadding = contentPadding
    ) {
        Text(text = text)
    }
}

@Composable
fun OutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 24.dp,
        vertical = 12.dp
    )
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        contentPadding = contentPadding
    ) {
        Text(text = text)
    }
}

@Composable
fun TextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 12.dp,
        vertical = 8.dp
    )
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        contentPadding = contentPadding
    ) {
        Text(text = text)
    }
}

@Composable
private fun ButtonPreviewContent() {
    Surface {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Basic Buttons",
                style = MaterialTheme.typography.titleMedium
            )

            FilledButton(
                text = "Filled Button",
                onClick = {},
                modifier = Modifier.fillMaxWidth()
            )

            FilledTonalButton(
                text = "Filled Tonal Button",
                onClick = {},
                modifier = Modifier.fillMaxWidth()
            )

            ElevatedButton(
                text = "Elevated Button",
                onClick = {},
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedButton(
                text = "Outlined Button",
                onClick = {},
                modifier = Modifier.fillMaxWidth()
            )

            TextButton(
                text = "Text Button",
                onClick = {},
                modifier = Modifier.fillMaxWidth()
            )

            FilledButton(
                text = "Disabled Button",
                onClick = {},
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(
    name = "Basic Buttons - Light Theme",
    showBackground = true
)
@Composable
private fun ButtonLightPreview() {
    AppTheme {
        ButtonPreviewContent()
    }
}

@Preview(
    name = "Basic Buttons - Dark Theme",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun ButtonDarkPreview() {
    AppTheme {
        ButtonPreviewContent()
    }
}