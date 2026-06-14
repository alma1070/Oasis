package com.almaslowcore.oasis.features.home.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almaslowcore.oasis.features.activity.domain.repository.ActivityRepository
import com.almaslowcore.oasis.features.activity.presentation.model.ActivityUiModel
import com.almaslowcore.oasis.features.gamification.domain.model.RewardRequest
import com.almaslowcore.oasis.features.gamification.domain.model.RewardSourceType
import com.almaslowcore.oasis.features.gamification.domain.repository.GamificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = false,
    val level: Int = 1,
    val currentXp: Int = 0,
    val xpToNext: Int = 100,
    val xpProgress: Float = 0f,
    val todayEarnedXp: Int = 0,
    val completedActivitiesCount: Int = 0,
    val totalActivitiesCount: Int = 0,
    val errorMessage: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val gamificationRepository: GamificationRepository,
    private val activityRepository: ActivityRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        val today = todayIsoDate()

        // 1. Lắng nghe trạng thái Game hóa (Level, XP)
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            gamificationRepository.observeUserStats()
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
                .collect { stats ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            level = stats.level,
                            currentXp = stats.currentXp,
                            xpToNext = stats.xpToNextLevel,
                            xpProgress = stats.xpProgress // Sử dụng luôn biến bạn đã viết trong UserStats
                        )
                    }
                }
        }

        // 2. Lắng nghe điểm XP nhận được trong ngày hôm nay
        viewModelScope.launch {
            gamificationRepository.observeRewardsByDate(today)
                .collect { rewards ->
                    // Tính tổng số XP lấy được trong list rewards hôm nay
                    val todayTotalXp = rewards.sumOf { it.xpAmount }
                    _uiState.update { it.copy(todayEarnedXp = todayTotalXp) }
                }
        }

        // 3. LẮNG NGHE DỮ LIỆU ACTIVITY THỰC TẾ
        viewModelScope.launch {
            activityRepository.observeActivitiesForDate(today)
                .collect { activities ->
                    // Tính tổng số Activity của hôm nay
                    val total = activities.size

                    // Đếm số Activity đã hoàn thành
                    // (Giả định ActivityPeriodDetailModel của bạn có trường isCompleted)
                    val completed = activities.count { it.summary.isCompleted }

                    // Cập nhật State cho giao diện
                    _uiState.update { currentState ->
                        currentState.copy(
                            totalActivitiesCount = total,
                            completedActivitiesCount = completed
                        )
                    }
                }
        }
    }

    fun onActivityCheck(activityId: String) {
        viewModelScope.launch {
            try {
                val today = todayIsoDate()

                // 1. Đánh dấu Activity là hoàn thành theo cấu trúc của ActivityRepository
                activityRepository.updateActivityCompletion(
                    activityId = activityId,
                    date = today,
                    isCompleted = true
                )

                // 2. Tạo Request cộng XP theo cấu trúc của GamificationRepository
                val rewardRequest = RewardRequest(
                    sourceType = RewardSourceType.ACTIVITY,
                    sourceId = activityId,
                    date = today,
                    xpAmount = 10, // Ví dụ: Cho 10 XP mỗi task
                    reason = "Completed activity from Home"
                )
                gamificationRepository.awardReward(rewardRequest)

            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message) }
            }
        }
    }

    // Helper: Lấy ngày hiện tại định dạng yyyy-MM-dd
    private fun todayIsoDate(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    }
}