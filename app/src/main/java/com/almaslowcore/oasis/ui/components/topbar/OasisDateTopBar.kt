package com.almaslowcore.oasis.ui.components.topbar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OasisDateTopBar(
    title: String,
    subtitle: String,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    onTitleClick: () -> Unit, // For the DatePicker
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {}
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = onPreviousClick) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, "Previous")
            }
        },
        title = {
            // Making the title clickable is a common pattern to open DatePicker
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable(onClick = onTitleClick)
            ) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                if (subtitle.isNotBlank()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        actions = {
            actions()
            IconButton(onClick = onNextClick) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, "Next")
            }
        }
    )
}