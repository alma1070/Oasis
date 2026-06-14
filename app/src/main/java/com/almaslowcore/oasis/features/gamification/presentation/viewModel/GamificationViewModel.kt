package com.almaslowcore.oasis.features.gamification.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almaslowcore.oasis.features.gamification.domain.model.RewardEvent
import com.almaslowcore.oasis.features.gamification.domain.model.UserStats
import com.almaslowcore.oasis.features.gamification.domain.repository.GamificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class GamificationViewModel @Inject constructor(
    private val repository: GamificationRepository
) : ViewModel() {

    val userStats: StateFlow<UserStats> = repository.observeUserStats()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserStats(
                level = 1,
                currentXp = 0,
                xpToNextLevel = 100,
                totalXp = 0,
                mindEnergy = 0,
                ecoEnergy = 0
            )
        )

    val recentRewards: StateFlow<List<RewardEvent>> = repository.observeRecentRewards(limit = 10)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
