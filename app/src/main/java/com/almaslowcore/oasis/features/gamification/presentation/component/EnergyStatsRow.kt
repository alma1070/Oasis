package com.almaslowcore.oasis.features.gamification.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EnergyStatsRow(mind: Int, eco: Int, totalXp: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        EnergyStatCard(
            modifier = Modifier.weight(1f),
            label = "Mind",
            value = mind.toString(),
            icon = Icons.Default.Psychology,
            color = MaterialTheme.colorScheme.tertiary
        )
        EnergyStatCard(
            modifier = Modifier.weight(1f),
            label = "Eco",
            value = eco.toString(),
            icon = Icons.Default.Eco,
            color = MaterialTheme.colorScheme.primary
        )
        EnergyStatCard(
            modifier = Modifier.weight(1f),
            label = "Total XP",
            value = totalXp.toString(),
            icon = Icons.Default.MilitaryTech,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}
