package com.almaslowcore.oasis.features.activity.presentation.model

data class ActivityScreenUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,

    val filterState: ActivityFilterState = ActivityFilterState(),
    val topBar: ActivityTopBarUiState = ActivityTopBarUiState(),

    val activities: List<ActivityUiModel> = emptyList(),
    val sections: List<ActivityListSectionUiModel> = emptyList(),

    val availableCategoryIds: List<String> = emptyList(),
    val availableLifeAreaIds: List<String> = emptyList(),

    val selectedActivityId: String? = null,

    val isFilterSheetOpen: Boolean = false,
    val isDatePickerOpen: Boolean = false,
    val isGroupByMenuOpen: Boolean = false
)
