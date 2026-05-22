package com.almaslowcore.oasis.features.activity.presentation.screen

import androidx.compose.runtime.Composable
import com.almaslowcore.oasis.ui.components.layout.ComingSoonScreen

@Composable
fun CreateActivityScreen() {
    ComingSoonScreen(
        title = "CreateActivityScreen",
        description = "Form tạo activity mới: tên, mô tả, habit/todo, repeat rule, category, tracking type, target value và unit."
    )
}