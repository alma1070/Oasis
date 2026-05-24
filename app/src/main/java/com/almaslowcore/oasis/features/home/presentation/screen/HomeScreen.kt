package com.almaslowcore.oasis.features.home.presentation.screen

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.almaslowcore.oasis.R
import com.almaslowcore.oasis.ui.components.layout.OasisScreen
import com.almaslowcore.oasis.ui.components.layout.SectionHeader

@Composable
fun HomeScreen() {
    OasisScreen(
    ) {
        Text("Eco preview coming soon")

        SectionHeader(
            title = "Today's Activities",
            subtitle = "List of activities",
            actionText = "View all",
            onActionClick = {
                // navController.navigate(...)
            }
        )

        Text("Mood check-in coming soon")
    }
}