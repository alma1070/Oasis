package com.almaslowcore.oasis.features.gamification.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import com.almaslowcore.oasis.features.gamification.presentation.model.RewardEventUiModel

@Composable
fun RewardEventItem(reward: RewardEventUiModel) {
    ListItem(
        headlineContent = { Text(reward.title) },
        supportingContent = {
            Column {
                Text(reward.subtitle)
                if (reward.energyText.isNotEmpty()) {
                    Text(reward.energyText, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelSmall)
                }
            }
        },
        trailingContent = {
            Column(horizontalAlignment = Alignment.End) {
                Text(reward.xpText, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                Text(reward.createdAtText, style = MaterialTheme.typography.labelSmall)
            }
        },
        leadingContent = {
            val icon = if (reward.subtitle.contains("Journal", ignoreCase = true)) Icons.Default.EditNote else Icons.Default.CheckCircle
            Icon(icon, contentDescription = null)
        }
    )
}
