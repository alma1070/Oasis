package com.almaslowcore.oasis.core.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almaslowcore.oasis.features.gamification.domain.model.RewardEvent
import com.almaslowcore.oasis.features.gamification.domain.model.UserStats
import com.almaslowcore.oasis.features.gamification.domain.repository.GamificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val gamificationRepository: GamificationRepository
) : ViewModel() {

    val userStats: StateFlow<UserStats?> = gamificationRepository
        .observeUserStats()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    private val _rewardEvents = MutableSharedFlow<RewardEvent>()
    val rewardEvents: SharedFlow<RewardEvent> = _rewardEvents.asSharedFlow()

    private val _levelUpEvents = MutableSharedFlow<Int>()
    val levelUpEvents: SharedFlow<Int> = _levelUpEvents.asSharedFlow()

    init {
        viewModelScope.launch {
            gamificationRepository.ensureUserStatsExists()
            
            // Observe recent rewards to trigger notifications
            gamificationRepository.observeRecentRewards(limit = 1)
                .drop(1) // Ignore initial load
                .collectLatest { rewards ->
                    rewards.firstOrNull()?.let {
                        _rewardEvents.emit(it)
                    }
                }
        }

        viewModelScope.launch {
            userStats
                .map { it?.level }
                .scan<Int?, Int?>(null) { prev, current ->
                    if (prev != null && current != null && current > prev) {
                        _levelUpEvents.emit(current)
                    }
                    current
                }
                .collectLatest { }
        }
    }
}
