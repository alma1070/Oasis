package com.almaslowcore.oasis.features.gamification.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun OasisStatusCard(ecoEnergy: Int) {
    val (stage, description) = when {
        ecoEnergy >= 80 -> "Green Oasis" to "A lush paradise teeming with life. Your consistency has created a self-sustaining world."
        ecoEnergy >= 30 -> "Small Garden" to "Flowers are blooming. Your efforts are starting to show real beauty."
        ecoEnergy >= 10 -> "Young Sprout" to "A tiny spark of life has emerged from the dry soil. Keep it growing."
        else -> "Dry Seed" to "The foundation is laid. Water it with your habits to see the first sprout."
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Oasis Status", style = MaterialTheme.typography.labelMedium)
            Text(stage, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(description, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
