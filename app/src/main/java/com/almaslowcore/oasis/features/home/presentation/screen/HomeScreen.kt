package com.almaslowcore.oasis.features.home.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.almaslowcore.oasis.R
import com.almaslowcore.oasis.features.gamification.presentation.component.TodaySummaryCard
import com.almaslowcore.oasis.features.gamification.presentation.component.XpProgressCard
import com.almaslowcore.oasis.features.home.presentation.viewModel.HomeUiState
import com.almaslowcore.oasis.features.home.presentation.viewModel.HomeViewModel
import com.almaslowcore.oasis.ui.components.layout.OasisScreen

@Composable
fun HomeRoute(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        uiState = uiState
    )
}

@Composable
fun HomeScreen(
    uiState: HomeUiState
) {
    OasisScreen(
        scrollable = true,
    ) {
        // --- 1. Lời chào ---
        Text(
            text = stringResource(R.string.home_greeting),
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- 2. Trạng thái Game hóa ---
        XpProgressCard(
            level = uiState.level,
            currentXp = uiState.currentXp,
            xpToNext = uiState.xpToNext,
            progress = uiState.xpProgress
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- 3. Tổng kết XP trong ngày ---
        TodaySummaryCard(
            todayXp = uiState.todayEarnedXp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- 4. Tiến độ Activity hôm nay ---
        val progress = if (uiState.totalActivitiesCount > 0) {
            uiState.completedActivitiesCount.toFloat() / uiState.totalActivitiesCount.toFloat()
        } else {
            0f
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = MaterialTheme.shapes.large
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.today_activity_progress),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${uiState.completedActivitiesCount} / ${uiState.totalActivitiesCount}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
                )
            }
        }
    }
}