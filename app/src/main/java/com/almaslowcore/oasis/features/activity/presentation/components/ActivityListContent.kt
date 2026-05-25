package com.almaslowcore.oasis.features.activity.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.almaslowcore.oasis.R
import com.almaslowcore.oasis.features.activity.presentation.model.ActivityGroupBy
import com.almaslowcore.oasis.features.activity.presentation.model.ActivityListSectionUiModel

@Composable
fun ActivityListContent(
    sections: List<ActivityListSectionUiModel>,
    groupBy: ActivityGroupBy,
    onActivityClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 16.dp,
        vertical = 12.dp
    )
) {
    val hasActivities = sections.any { section ->
        section.activities.isNotEmpty()
    }

    if (!hasActivities) {
        ActivityEmptyContent(
            modifier = modifier.fillMaxSize()
        )
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (groupBy == ActivityGroupBy.NONE) {
            val activities = sections
                .flatMap { section ->
                    section.activities
                }

            items(
                items = activities,
                key = { activity ->
                    activity.id
                }
            ) { activity ->
                ActivityCard(
                    activity = activity,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onActivityClick(activity.id)
                    }
                )
            }
        } else {
            sections.forEach { section ->
                if (section.activities.isNotEmpty()) {
                    item(
                        key = "section-${section.title}"
                    ) {
                        ActivitySectionHeader(
                            title = section.title
                        )
                    }

                    items(
                        items = section.activities,
                        key = { activity ->
                            activity.id
                        }
                    ) { activity ->
                        ActivityCard(
                            activity = activity,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                onActivityClick(activity.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ActivityEmptyContent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.no_activities_found),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}