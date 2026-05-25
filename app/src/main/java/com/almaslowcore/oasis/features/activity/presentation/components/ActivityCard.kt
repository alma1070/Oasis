package com.almaslowcore.oasis.features.activity.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.R
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.almaslowcore.oasis.features.activity.presentation.model.ActivitySubtaskUiModel
import com.almaslowcore.oasis.features.activity.presentation.model.ActivityUiMeasurableMode
import com.almaslowcore.oasis.features.activity.presentation.model.ActivityUiModel
import com.almaslowcore.oasis.features.activity.presentation.model.ActivityUiTrackingType
import com.almaslowcore.oasis.ui.components.cards.BasisCard
import com.almaslowcore.oasis.ui.components.cards.BasisCardType
import com.almaslowcore.oasis.ui.theme.AppTheme
import kotlin.math.roundToInt
import com.almaslowcore.oasis.R as R1

@Composable
fun ActivityCard(
    activity: ActivityUiModel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val cardType = if (activity.isCompleted) {
        BasisCardType.Outlined
    } else {
        BasisCardType.Filled
    }

    BasisCard(
        modifier = modifier
            .clickable(onClick = onClick)
            .alpha(
                if (activity.isCompleted) 0.72f else 1f
            ),
        type = cardType,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ActivityCardHeader(
            activity = activity
        )

        when {
            activity.trackingType == ActivityUiTrackingType.MEASURABLE &&
                    activity.measurableMode == ActivityUiMeasurableMode.NUMERIC -> {
                NumericProgressSection(activity = activity)
            }

            activity.trackingType == ActivityUiTrackingType.MEASURABLE &&
                    activity.measurableMode == ActivityUiMeasurableMode.CHECKLIST -> {
                ChecklistProgressSection(activity = activity)
            }
        }

        ActivityCardFooter(activity = activity)
    }
}

@Composable
private fun ActivityCardHeader(
    activity: ActivityUiModel
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            ActivityCardTitle(activity = activity)

            ActivityCardMeta(activity = activity)
        }

        ActivityTypeLabel(
            isHabit = activity.isHabit
        )
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
private fun NumericProgressSection(
    activity: ActivityUiModel
) {
    val currentValue = activity.currentValue ?: 0.0
    val targetValue = activity.targetValue ?: 0.0

    val progress = activity.progress
        ?: if (targetValue > 0.0) {
            (currentValue / targetValue)
                .toFloat()
                .coerceIn(0f, 1f)
        } else {
            0f
        }

    val progressText = if (targetValue > 0.0) {
        "${currentValue.toCleanText()} / ${targetValue.toCleanText()} ${activity.unit.orEmpty()}"
    } else {
        "Chưa đặt mục tiêu"
    }

    ProgressBlock(
        leadingText = progressText,
        trailingText = progress.toPercentageText(),
        progress = progress
    )
}

@Composable
private fun ChecklistProgressSection(
    activity: ActivityUiModel
) {
    val progress = activity.progress
        ?: if (activity.totalSubtaskCount > 0) {
            (activity.completedSubtaskCount.toFloat() / activity.totalSubtaskCount.toFloat())
                .coerceIn(0f, 1f)
        } else {
            0f
        }

    ProgressBlock(
        leadingText = "${activity.completedSubtaskCount} / ${activity.totalSubtaskCount} subtasks",
        trailingText = progress.toPercentageText(),
        progress = progress
    )
}

@Composable
private fun ProgressBlock(
    leadingText: String,
    trailingText: String,
    progress: Float
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = leadingText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = trailingText,
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
            add("${activity.streakCount} ${stringResource(R1.string.day)} streak")
        }

        when {
            activity.trackingType == ActivityUiTrackingType.YES_NO -> {
                add("${stringResource(R1.string.yes)} / ${stringResource(R1.string.no)}")
            }

            activity.measurableMode == ActivityUiMeasurableMode.NUMERIC -> {
                add(stringResource(R1.string.measurable))
            }

            activity.measurableMode == ActivityUiMeasurableMode.CHECKLIST -> {
                add(stringResource(R1.string.checklist))
            }
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
            text = if (isHabit) stringResource(R1.string.habit) else stringResource(R1.string.task),
            style = MaterialTheme.typography.labelSmall
        )
    }
}

private fun Double.toCleanText(): String {
    return if (this % 1.0 == 0.0) {
        this.toInt().toString()
    } else {
        this.toString()
    }
}

private fun Float.toPercentageText(): String {
    return "${(this * 100).roundToInt()}%"
}