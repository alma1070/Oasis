package com.almaslowcore.oasis.features.gamification.domain.usecase

import com.almaslowcore.oasis.features.gamification.domain.model.RewardRequest
import com.almaslowcore.oasis.features.gamification.domain.model.RewardSourceType
import com.almaslowcore.oasis.features.gamification.domain.repository.GamificationRepository
import javax.inject.Inject

class AwardActivityRewardUseCase @Inject constructor(
    private val gamificationRepository: GamificationRepository
) {
    suspend operator fun invoke(
        activityId: String,
        activityTitle: String,
        isHabit: Boolean,
        date: String,
        isCompleted: Boolean
    ): Boolean {
        if (!isCompleted) return false

        val xpAmount: Int
        val ecoEnergyAmount: Int
        val reason: String

        if (isHabit) {
            xpAmount = 15
            ecoEnergyAmount = 2
            reason = "Completed habit: $activityTitle"
        } else {
            xpAmount = 20
            ecoEnergyAmount = 1
            reason = "Completed task: $activityTitle"
        }

        val request = RewardRequest(
            sourceType = RewardSourceType.ACTIVITY,
            sourceId = activityId,
            date = date,
            xpAmount = xpAmount,
            mindEnergyAmount = 0,
            ecoEnergyAmount = ecoEnergyAmount,
            reason = reason
        )

        return gamificationRepository.awardReward(request)
    }
}
