package com.almaslowcore.oasis.features.gamification.presentation.model

data class ProfileUiState(
    val isLoading: Boolean = false,
    val level: Int = 1,
    val currentXp: Int = 0,
    val xpToNextLevel: Int = 100,
    val xpProgress: Float = 0f,
    val totalXp: Int = 0,
    val mindEnergy: Int = 0,
    val ecoEnergy: Int = 0,
    val todayXpEarned: Int = 0,
    val recentRewards: List<RewardEventUiModel> = emptyList(),
    val errorMessage: String? = null
)
