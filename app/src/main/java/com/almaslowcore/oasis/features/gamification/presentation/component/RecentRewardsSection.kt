package com.almaslowcore.oasis.features.gamification.presentation.component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.almaslowcore.oasis.features.gamification.presentation.model.RewardEventUiModel

fun LazyListScope.RecentRewardsSection(rewards: List<RewardEventUiModel>) {
    item {
        Text(
            text = "Recent Rewards",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
    
    if (rewards.isEmpty()) {
        item {
            Text(
                text = "Complete an activity or write a journal check-in to earn your first XP.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        items(rewards) { reward ->
            RewardEventItem(reward)
        }
    }
}
