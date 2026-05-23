package com.almaslowcore.oasis.features.activity.presentation.screen


import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.almaslowcore.oasis.features.activity.presentation.components.ActivityTab
import com.almaslowcore.oasis.features.activity.presentation.components.ActivityTabs
import com.almaslowcore.oasis.ui.components.layout.OasisScreen

@Composable
fun ActivitiesScreen() {
    var selectedTabIndex by rememberSaveable {
        mutableIntStateOf(ActivityTab.Today.ordinal)
    }

    val selectedTab = ActivityTab.entries[selectedTabIndex]

    OasisScreen(
        title = "Activities",
        subtitle = "Your habits and tasks for today.",
        scrollable = false
    ) {
        ActivityTabs(
            selectedTab = selectedTab,
            onTabSelected = { tab ->
                selectedTabIndex = tab.ordinal
            }
        )

        // Activity list theo selectedTab
    }

}