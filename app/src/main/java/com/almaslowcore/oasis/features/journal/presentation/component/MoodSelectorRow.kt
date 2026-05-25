package com.almaslowcore.oasis.features.journal.presentation.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.almaslowcore.oasis.features.journal.domain.model.MoodType

@Composable
fun MoodSelectorRow(
    selectedMood: MoodType,
    onMoodSelected: (MoodType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        JournalSectionTitle(text = "Mood")

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(horizontal = 2.dp)
        ) {
            items(
                items = MoodType.entries,
                key = { mood -> mood.name }
            ) { mood ->
                MoodOptionItem(
                    mood = mood,
                    selected = selectedMood == mood,
                    onClick = {
                        onMoodSelected(mood)
                    }
                )
            }
        }
    }
}

@Composable
private fun MoodOptionItem(
    mood: MoodType,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.06f else 1f,
        label = "Mood option scale"
    )

    val borderWidth by animateDpAsState(
        targetValue = if (selected) 2.dp else 1.dp,
        label = "Mood option border"
    )

    Surface(
        modifier = modifier
            .width(92.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        color = if (selected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceContainer
        },
        border = BorderStroke(
            width = borderWidth,
            color = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outlineVariant
            }
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.width(92.dp)
        ) {
            Text(
                text = mood.emoji,
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = mood.defaultLabel,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                color = if (selected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}