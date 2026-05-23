package com.almaslowcore.oasis.features.activity.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.almaslowcore.oasis.ui.components.cards.BasisCard
import com.almaslowcore.oasis.ui.components.cards.BasisCardType
import com.almaslowcore.oasis.ui.theme.AppTheme

enum class ActivityTrackingType {
    YES_NO,
    MEASURABLE
}

data class ActivityUiModel(
    val id: String,
    val title: String,
    val description: String? = null,
    val isHabit: Boolean,
    val trackingType: ActivityTrackingType,
    val isCompleted: Boolean = false,
    val category: String? = null,
    val lifeArea: String? = null,
    val currentValue: Float? = null,
    val targetValue: Float? = null,
    val unit: String? = null,
    val streakCount: Int? = null,
    val dueText: String? = null,
    val repeatText: String? = null
)

/*
val isOverdue: Boolean
val priority: ActivityPriority
val icon: ImageVector?
val progressColor
 */

// sau này có thể tách ra ActivityUiModel và ActivityTrackingType

@Composable
fun ActivityCard(
    activity: ActivityUiModel,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    onCheckedChange: ((Boolean) -> Unit)? = null
) {
    val cardType = if (activity.isCompleted) {
        BasisCardType.Outlined
    } else {
        BasisCardType.Filled
    }

    BasisCard(
        modifier = modifier
            .then(
                if (onClick != null) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            )
            .alpha(
                if (activity.isCompleted) 0.72f else 1f
            ),
        type = cardType,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Checkbox(
                checked = activity.isCompleted,
                onCheckedChange = onCheckedChange
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ActivityCardTitle(activity = activity)

                ActivityCardMeta(activity = activity)

                activity.description?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            ActivityTypeLabel(
                isHabit = activity.isHabit
            )
        }

        if (activity.trackingType == ActivityTrackingType.MEASURABLE) {
            ActivityProgressSection(activity = activity)
        }

        ActivityCardFooter(activity = activity)
    }
}

@Composable
private fun ActivityCardTitle(
    activity: ActivityUiModel
) {
    Text(
        text = activity.title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface,
        textDecoration = if (activity.isCompleted) {
            TextDecoration.LineThrough
        } else {
            TextDecoration.None
        }
    )
}

@Composable
private fun ActivityCardMeta(
    activity: ActivityUiModel
) {
    val metaItems = buildList {
        activity.category?.let { add(it) }
        activity.lifeArea?.let { add(it) }
        activity.dueText?.let { add(it) }
        activity.repeatText?.let { add(it) }
    }

    if (metaItems.isNotEmpty()) {
        Text(
            text = metaItems.joinToString(" · "),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ActivityProgressSection(
    activity: ActivityUiModel
) {
    val currentValue = activity.currentValue ?: 0f
    val targetValue = activity.targetValue ?: 0f

    val progress = if (targetValue > 0f) {
        (currentValue / targetValue).coerceIn(0f, 1f)
    } else {
        0f
    }

    val progressText = if (targetValue > 0f) {
        "${currentValue.toCleanText()} / ${targetValue.toCleanText()} ${activity.unit.orEmpty()}"
    } else {
        "No target set"
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = progressText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
        )
    }
}

@Composable
private fun ActivityCardFooter(
    activity: ActivityUiModel
) {
    val footerItems = buildList {
        if (activity.isHabit && activity.streakCount != null) {
            add("${activity.streakCount} day streak")
        }

        if (activity.trackingType == ActivityTrackingType.YES_NO) {
            add("Yes / No")
        } else {
            add("Measurable")
        }
    }

    if (footerItems.isNotEmpty()) {
        Text(
            text = footerItems.joinToString(" · "),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun ActivityTypeLabel(
    isHabit: Boolean
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = if (isHabit) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.secondaryContainer
        },
        contentColor = if (isHabit) {
            MaterialTheme.colorScheme.onPrimaryContainer
        } else {
            MaterialTheme.colorScheme.onSecondaryContainer
        }
    ) {
        Text(
            modifier = Modifier.padding(
                horizontal = 8.dp,
                vertical = 4.dp
            ),
            text = if (isHabit) "Habit" else "Task",
            style = MaterialTheme.typography.labelSmall
        )
    }
}

private fun Float.toCleanText(): String {
    return if (this % 1f == 0f) {
        this.toInt().toString()
    } else {
        this.toString()
    }
}

@Preview(
    name = "ActivityCard - Light",
    showBackground = true
)
@Composable
private fun ActivityCardLightPreview() {
    AppTheme(
        darkTheme = false,
        dynamicColor = false
    ) {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActivityCard(
                    activity = ActivityUiModel(
                        id = "1",
                        title = "Read book",
                        description = "Continue reading the Android UI chapter.",
                        isHabit = true,
                        trackingType = ActivityTrackingType.MEASURABLE,
                        category = "Study",
                        lifeArea = "Personal Growth",
                        currentValue = 20f,
                        targetValue = 30f,
                        unit = "pages",
                        streakCount = 7,
                        repeatText = "Daily"
                    ),
                    onClick = {},
                    onCheckedChange = {}
                )

                ActivityCard(
                    activity = ActivityUiModel(
                        id = "2",
                        title = "Submit assignment",
                        isHabit = false,
                        trackingType = ActivityTrackingType.YES_NO,
                        category = "School",
                        dueText = "Today"
                    ),
                    onClick = {},
                    onCheckedChange = {}
                )

                ActivityCard(
                    activity = ActivityUiModel(
                        id = "3",
                        title = "Drink water",
                        isHabit = true,
                        trackingType = ActivityTrackingType.YES_NO,
                        isCompleted = true,
                        category = "Health",
                        streakCount = 5,
                        repeatText = "Daily"
                    ),
                    onClick = {},
                    onCheckedChange = {}
                )
            }
        }
    }
}

@Preview(
    name = "ActivityCard - Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun ActivityCardDarkPreview() {
    AppTheme(
        darkTheme = true,
        dynamicColor = false
    ) {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActivityCard(
                    activity = ActivityUiModel(
                        id = "1",
                        title = "Read book",
                        description = "Continue reading the Android UI chapter.",
                        isHabit = true,
                        trackingType = ActivityTrackingType.MEASURABLE,
                        category = "Study",
                        lifeArea = "Personal Growth",
                        currentValue = 20f,
                        targetValue = 30f,
                        unit = "pages",
                        streakCount = 7,
                        repeatText = "Daily"
                    ),
                    onClick = {},
                    onCheckedChange = {}
                )

                ActivityCard(
                    activity = ActivityUiModel(
                        id = "2",
                        title = "Submit assignment",
                        isHabit = false,
                        trackingType = ActivityTrackingType.YES_NO,
                        category = "School",
                        dueText = "Today"
                    ),
                    onClick = {},
                    onCheckedChange = {}
                )

                ActivityCard(
                    activity = ActivityUiModel(
                        id = "3",
                        title = "Drink water",
                        isHabit = true,
                        trackingType = ActivityTrackingType.YES_NO,
                        isCompleted = true,
                        category = "Health",
                        streakCount = 5,
                        repeatText = "Daily"
                    ),
                    onClick = {},
                    onCheckedChange = {}
                )
            }
        }
    }
}