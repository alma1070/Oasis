package com.almaslowcore.oasis.features.activity.presentation.model

import com.almaslowcore.oasis.features.activity.presentation.components.ActivityTab

data class ActivityScreenUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val selectedTab: ActivityTab = ActivityTab.Today,
    val activities: List<ActivityUiModel> = emptyList(),
    val selectedActivityId: String? = null
)
