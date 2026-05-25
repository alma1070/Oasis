package com.almaslowcore.oasis.features.activity.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.almaslowcore.oasis.R
import com.almaslowcore.oasis.features.activity.presentation.components.ActivityCard
import com.almaslowcore.oasis.features.activity.presentation.components.ActivityProgressDialog
import com.almaslowcore.oasis.features.activity.presentation.viewmodel.ActivityViewModel
import com.almaslowcore.oasis.ui.components.layout.EmptyState
import com.almaslowcore.oasis.ui.components.layout.ErrorState
import com.almaslowcore.oasis.ui.components.layout.LoadingState
import com.almaslowcore.oasis.ui.components.layout.OasisScreen

@Composable
fun ActivitiesScreen(
    viewModel: ActivityViewModel = hiltViewModel(),
    onCreateActivity: () -> Unit = {},
    onActivityClick: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val selectedActivity = uiState.activities
        .firstOrNull { activity ->
            activity.id == uiState.selectedActivityId
        }

    OasisScreen(
        scrollable = false
    ) {
        when {
            uiState.isLoading -> {
                LoadingState(
                    message = stringResource(R.string.loadingActivity)
                )
            }

            uiState.errorMessage != null -> {
                ErrorState(
                    title = stringResource(R.string.actLoadError),
                    message = uiState.errorMessage
                )
            }

            uiState.activities.isEmpty() -> {
                EmptyState(
                    title = stringResource(R.string.noActivity),
                    message = stringResource(R.string.actCreateToBegin),
                    actionText = stringResource(R.string.createActivity),
                    onActionClick = onCreateActivity
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = uiState.activities,
                        key = { it.id }
                    ) { activity ->
                        ActivityCard(
                            activity = activity,
                            onClick = {
                                viewModel.openProgressDialog(activity.id)
                            }
                        )
                    }
                }
            }
        }

        selectedActivity?.let { activity ->
            ActivityProgressDialog(
                activity = activity,
                onDismiss = viewModel::dismissProgressDialog,
                onCompleteYesNo = viewModel::completeYesNoActivity,
                onSaveNumeric = viewModel::saveNumericProgress,
                onSaveChecklist = viewModel::saveChecklistProgress
            )
        }
    }
}
