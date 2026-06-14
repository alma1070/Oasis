package com.almaslowcore.oasis.features.gamification.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.almaslowcore.oasis.features.gamification.presentation.component.*
import com.almaslowcore.oasis.features.gamification.presentation.model.ProfileUiState
import com.almaslowcore.oasis.features.gamification.presentation.viewModel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text("Profile & Growth") }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage ?: "Unknown error",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp).align(Alignment.Center)
                )
            } else {
                ProfileContent(uiState)
            }
        }
    }
}

@Composable
private fun ProfileContent(uiState: ProfileUiState) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { ProfileHeader(uiState.level) }
        item {
            XpProgressCard(
                level = uiState.level,
                currentXp = uiState.currentXp,
                xpToNext = uiState.xpToNextLevel,
                progress = uiState.xpProgress
            )
        }
        item {
            EnergyStatsRow(
                mind = uiState.mindEnergy,
                eco = uiState.ecoEnergy,
                totalXp = uiState.totalXp
            )
        }
        item { TodaySummaryCard(uiState.todayXpEarned) }
        item { OasisStatusCard(uiState.ecoEnergy) }
        
        RecentRewardsSection(uiState.recentRewards)
        
        item { Spacer(modifier = Modifier.height(32.dp)) }
    }
}
