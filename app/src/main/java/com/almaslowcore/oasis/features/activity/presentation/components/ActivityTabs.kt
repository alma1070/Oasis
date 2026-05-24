package com.almaslowcore.oasis.features.activity.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.almaslowcore.oasis.ui.theme.AppTheme

enum class ActivityTab(
    val label: String
) {
    Today("Hôm nay"),
    Habits("Thói quen"),
    Tasks("Việc cần làm"),
    Completed("Hoàn thành")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityTabs(
    selectedTab: ActivityTab,
    onTabSelected: (ActivityTab) -> Unit,
    modifier: Modifier = Modifier,
    tabs: List<ActivityTab> = ActivityTab.entries
) {
    val selectedTabIndex = tabs
        .indexOf(selectedTab)
        .coerceAtLeast(0)

    PrimaryTabRow(
        selectedTabIndex = selectedTabIndex,
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.primary
    ) {
        tabs.forEach { tab ->
            Tab(
                selected = selectedTab == tab,
                onClick = {
                    onTabSelected(tab)
                },
                text = {
                    Text(
                        text = tab.label,
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelMedium,
                    )
                },
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(
    name = "ActivityTabs - Light",
    showBackground = true
)
@Composable
private fun ActivityTabsLightPreview() {
    AppTheme(
        darkTheme = false,
        dynamicColor = false
    ) {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                ActivityTabs(
                    selectedTab = ActivityTab.Today,
                    onTabSelected = {}
                )
            }
        }
    }
}

@Preview(
    name = "ActivityTabs - Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun ActivityTabsDarkPreview() {
    AppTheme(
        darkTheme = true,
        dynamicColor = false
    ) {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                ActivityTabs(
                    selectedTab = ActivityTab.Today,
                    onTabSelected = {}
                )
            }
        }
    }
}