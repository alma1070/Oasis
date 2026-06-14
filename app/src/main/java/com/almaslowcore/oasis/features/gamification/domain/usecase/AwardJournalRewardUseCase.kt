package com.almaslowcore.oasis.features.gamification.domain.usecase

import com.almaslowcore.oasis.features.gamification.domain.model.RewardRequest
import com.almaslowcore.oasis.features.gamification.domain.model.RewardSourceType
import com.almaslowcore.oasis.features.gamification.domain.repository.GamificationRepository
import javax.inject.Inject

class AwardJournalRewardUseCase @Inject constructor(
    private val gamificationRepository: GamificationRepository
) {
    suspend operator fun invoke(
        journalEntryId: Long,
        moodScore: Int,
        note: String,
        date: String?
    ): Boolean {
        var xpAmount = 10
        var mindEnergyAmount = 2
        val ecoEnergyAmount = 0
        val reason = "Journal check-in"

        // Bonus for high mood
        if (moodScore >= 4) {
            xpAmount += 5
            mindEnergyAmount += 1
        }

        // Bonus for detailed note
        if (note.trim().length >= 30) {
            xpAmount += 2
        }

        val request = RewardRequest(
            sourceType = RewardSourceType.JOURNAL,
            sourceId = journalEntryId.toString(),
            date = date,
            xpAmount = xpAmount,
            mindEnergyAmount = mindEnergyAmount,
            ecoEnergyAmount = ecoEnergyAmount,
            reason = reason
        )

        return gamificationRepository.awardReward(request)
    }
}
