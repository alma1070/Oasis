package com.almaslowcore.oasis.features.activity.presentation.screen


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.almaslowcore.oasis.R
import com.almaslowcore.oasis.ui.components.buttons.Fab
import com.almaslowcore.oasis.ui.components.layout.EmptyState
import com.almaslowcore.oasis.ui.components.layout.ErrorState
import com.almaslowcore.oasis.ui.components.layout.LoadingState
import com.almaslowcore.oasis.ui.components.layout.OasisScreen
import com.almaslowcore.oasis.ui.components.layout.SectionHeader

@Composable
fun ActivitiesScreen() {
    OasisScreen(
        title = stringResource(R.string.activities),
        subtitle = "Hiển thị danh sách activity gồm habit và todo, có bộ lọc Today, Habits, Tasks và Completed.",
        floatingActionButton = {
            Fab(
                icon = Icons.Default.Add,
                contentDescription = "Add",
                onClick = {}
            )
        }
    ) {
        SectionHeader(
            title = "Today's Activities",
            subtitle = "List of activities",
            actionText = "View all",
            onActionClick = {
                // navController.navigate(...)
            }
        )
        val isLoading = false
        val hasError = false
        val activities = emptyList<String>()
        when {
            isLoading -> {
                LoadingState(
                    message = "Loading activities..."
                )
            }
            hasError -> {
                ErrorState(
                    title = "Unable to load activities",
                    message = "Something interrupted your data. Naturally.",
                    onActionClick = {
                        // retry
                    }
                )
            }

            activities.isEmpty() -> {
                EmptyState(
                    title = "No activities yet",
                    message = "Create your first activity to start building your day.",
                    actionText = "Create activity",
                    onActionClick = {
                        // navigate
                    }
                )
            }



            else -> {
                // Activity list
            }
        }
    }

}