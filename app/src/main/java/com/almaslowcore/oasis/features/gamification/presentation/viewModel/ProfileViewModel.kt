package com.almaslowcore.oasis.features.gamification.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almaslowcore.oasis.features.gamification.domain.model.RewardEvent
import com.almaslowcore.oasis.features.gamification.domain.repository.GamificationRepository
import com.almaslowcore.oasis.features.gamification.presentation.model.ProfileUiState
import com.almaslowcore.oasis.features.gamification.presentation.model.RewardEventUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val gamificationRepository: GamificationRepository
) : ViewModel() {

    private val todayDate = LocalDate.now().toString()

    val uiState: StateFlow<ProfileUiState> = combine(
        gamificationRepository.observeUserStats(),
        gamificationRepository.observeRecentRewards(limit = 10),
        gamificationRepository.observeRewardsByDate(todayDate)
    ) { stats, recentRewards, todayRewards ->
        ProfileUiState(
            isLoading = false,
            level = stats.level,
            currentXp = stats.currentXp,
            xpToNextLevel = stats.xpToNextLevel,
            xpProgress = stats.xpProgress,
            totalXp = stats.totalXp,
            mindEnergy = stats.mindEnergy,
            ecoEnergy = stats.ecoEnergy,
            todayXpEarned = todayRewards.sumOf { it.xpAmount },
            recentRewards = recentRewards.map { it.toUiModel() }
        )
    }
    .onStart { emit(ProfileUiState(isLoading = true)) }
    .catch { e -> emit(ProfileUiState(errorMessage = e.message ?: "Unknown error")) }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProfileUiState(isLoading = true)
    )

    init {
        viewModelScope.launch {
            gamificationRepository.ensureUserStatsExists()
        }
    }

    private fun RewardEvent.toUiModel(): RewardEventUiModel {
        val energyParts = mutableListOf<String>()
        if (mindEnergyAmount > 0) energyParts.add("+$mindEnergyAmount Mind")
        if (ecoEnergyAmount > 0) energyParts.add("+$ecoEnergyAmount Eco")

        return RewardEventUiModel(
            id = id,
            title = reason,
            subtitle = sourceType.name.lowercase().replaceFirstChar { it.uppercase() },
            xpText = "+$xpAmount XP",
            energyText = energyParts.joinToString(" · "),
            createdAtText = formatTimestamp(createdAt)
        )
    }

    private fun formatTimestamp(timestamp: Long): String {
        val formatter = DateTimeFormatter.ofPattern("MMM d, HH:mm", Locale.getDefault())
        return Instant.ofEpochMilli(timestamp)
            .atZone(ZoneId.systemDefault())
            .format(formatter)
    }
}
