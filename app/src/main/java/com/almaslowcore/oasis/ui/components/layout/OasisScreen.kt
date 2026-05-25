package com.almaslowcore.oasis.ui.components.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun OasisScreen(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 20.dp,
        vertical = 16.dp
    ),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(16.dp),
    scrollable: Boolean = true, // false if you want to use LazyColumn
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .then(
                if (scrollable) {
                    Modifier.verticalScroll(rememberScrollState())
                } else {
                    Modifier
                }
            )
            .padding(contentPadding),
        verticalArrangement = verticalArrangement
    ) {
        content()
    }
}