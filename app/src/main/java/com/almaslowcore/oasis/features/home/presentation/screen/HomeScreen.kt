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
        title = stringResource(R.string.welcome_back),
        subtitle = "Hiển thị tổng quan hôm nay: activity cần làm, mood hiện tại, tiến trình EXP, eco preview và các hành động nhanh."
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