package com.almaslowcore.oasis.features.activity.presentation.screen

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.almaslowcore.oasis.features.activity.presentation.components.ActivityCard
import com.almaslowcore.oasis.features.activity.presentation.components.ActivityTab
import com.almaslowcore.oasis.features.activity.presentation.components.ActivityTabs
import com.almaslowcore.oasis.features.activity.presentation.components.ActivityTrackingType
import com.almaslowcore.oasis.features.activity.presentation.components.ActivityUiModel
import com.almaslowcore.oasis.ui.components.layout.EmptyState
import com.almaslowcore.oasis.ui.components.layout.OasisScreen
import com.almaslowcore.oasis.ui.components.layout.SectionHeader
import com.almaslowcore.oasis.ui.theme.AppTheme

@Composable
fun ActivitiesScreen() {
    var selectedTabIndex by rememberSaveable {
        mutableIntStateOf(ActivityTab.Today.ordinal)
    }

    val selectedTab = ActivityTab.entries.getOrElse(selectedTabIndex) {
        ActivityTab.Today
    }

    val activities = remember {
        mutableStateListOf<ActivityUiModel>().apply {
            addAll(fakeActivities)
        }
    }

    val filteredActivities = activities.filterByTab(selectedTab)

    OasisScreen(
        scrollable = false
    ) {
        ActivityTabs(
            selectedTab = selectedTab,
            onTabSelected = { tab ->
                selectedTabIndex = tab.ordinal
            },
            modifier = Modifier.fillMaxWidth()
        )

        SectionHeader(
            title = selectedTab.label,
            subtitle = buildActivityCountText(
                count = filteredActivities.size,
                selectedTab = selectedTab
            )
        )

        if (filteredActivities.isEmpty()) {
            EmptyState(
                title = emptyStateTitle(selectedTab),
                message = emptyStateMessage(selectedTab),
                actionText = "Tạo hoạt động",
                onActionClick = {
                    // TODO: Navigate to CreateActivityScreen
                }
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = filteredActivities,
                    key = { activity -> activity.id }
                ) { activity ->
                    ActivityCard(
                        activity = activity,
                        onClick = {
                            // TODO: Navigate to ActivityDetailScreen
                        },
                        onCheckedChange = { isChecked ->
                            val index = activities.indexOfFirst {
                                it.id == activity.id
                            }

                            if (index != -1) {
                                activities[index] = activities[index].copy(
                                    isCompleted = isChecked
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

private fun List<ActivityUiModel>.filterByTab(
    tab: ActivityTab
): List<ActivityUiModel> {
    return when (tab) {
        ActivityTab.Today -> {
            filter {
                !it.isCompleted
            }
        }

        ActivityTab.Habits -> {
            filter {
                it.isHabit && !it.isCompleted
            }
        }

        ActivityTab.Tasks -> {
            filter {
                !it.isHabit && !it.isCompleted
            }
        }

        ActivityTab.Completed -> {
            filter {
                it.isCompleted
            }
        }
    }
}

private fun buildActivityCountText(
    count: Int,
    selectedTab: ActivityTab
): String {
    return when (selectedTab) {
        ActivityTab.Today -> "$count hoạt động cần chú ý hôm nay"
        ActivityTab.Habits -> "$count thói quen đang theo dõi"
        ActivityTab.Tasks -> "$count việc cần làm"
        ActivityTab.Completed -> "$count hoạt động đã hoàn thành"
    }
}

private fun emptyStateTitle(
    tab: ActivityTab
): String {
    return when (tab) {
        ActivityTab.Today -> "Hôm nay chưa có hoạt động"
        ActivityTab.Habits -> "Chưa có thói quen"
        ActivityTab.Tasks -> "Chưa có việc cần làm"
        ActivityTab.Completed -> "Chưa hoàn thành hoạt động nào"
    }
}

private fun emptyStateMessage(
    tab: ActivityTab
): String {
    return when (tab) {
        ActivityTab.Today -> "Tạo một hoạt động nhỏ để bắt đầu ngày của bạn nhẹ nhàng hơn."
        ActivityTab.Habits -> "Thêm một thói quen để xây dựng nhịp sống bền vững."
        ActivityTab.Tasks -> "Thêm một việc cần làm để giữ mọi thứ rõ ràng hơn."
        ActivityTab.Completed -> "Khi bạn hoàn thành hoạt động, chúng sẽ xuất hiện ở đây."
    }
}

private val fakeActivities = listOf(
    ActivityUiModel(
        id = "activity_1",
        title = "Đọc sách",
        description = "Đọc tiếp chương về Android UI.",
        isHabit = true,
        trackingType = ActivityTrackingType.MEASURABLE,
        category = "Học tập",
        lifeArea = "Phát triển cá nhân",
        currentValue = 20f,
        targetValue = 30f,
        unit = "trang",
        streakCount = 7,
        repeatText = "Hằng ngày"
    ),
    ActivityUiModel(
        id = "activity_2",
        title = "Nộp bài tập",
        description = "Hoàn thiện phần UI components cho project.",
        isHabit = false,
        trackingType = ActivityTrackingType.YES_NO,
        category = "Trường học",
        lifeArea = "Học tập",
        dueText = "Hôm nay"
    ),
    ActivityUiModel(
        id = "activity_3",
        title = "Uống nước",
        isHabit = true,
        trackingType = ActivityTrackingType.MEASURABLE,
        category = "Sức khỏe",
        lifeArea = "Health",
        currentValue = 1200f,
        targetValue = 2000f,
        unit = "ml",
        streakCount = 5,
        repeatText = "Hằng ngày"
    ),
    ActivityUiModel(
        id = "activity_4",
        title = "Dọn bàn học",
        isHabit = false,
        trackingType = ActivityTrackingType.YES_NO,
        category = "Không gian sống",
        lifeArea = "Environment",
        dueText = "Hôm nay"
    ),
    ActivityUiModel(
        id = "activity_5",
        title = "Thiền ngắn",
        description = "Dành vài phút để thở và quan sát lại bản thân.",
        isHabit = true,
        trackingType = ActivityTrackingType.YES_NO,
        category = "Tinh thần",
        lifeArea = "Mind",
        streakCount = 3,
        repeatText = "Hằng ngày"
    ),
    ActivityUiModel(
        id = "activity_6",
        title = "Hoàn thành wireframe ActivityScreen",
        isHabit = false,
        trackingType = ActivityTrackingType.MEASURABLE,
        category = "Project",
        lifeArea = "Career",
        currentValue = 3f,
        targetValue = 5f,
        unit = "phần",
        dueText = "Tuần này"
    ),
    ActivityUiModel(
        id = "activity_7",
        title = "Đi bộ",
        isHabit = true,
        trackingType = ActivityTrackingType.MEASURABLE,
        isCompleted = true,
        category = "Sức khỏe",
        lifeArea = "Health",
        currentValue = 5000f,
        targetValue = 5000f,
        unit = "bước",
        streakCount = 10,
        repeatText = "Hằng ngày"
    ),
    ActivityUiModel(
        id = "activity_8",
        title = "Viết journal buổi tối",
        isHabit = true,
        trackingType = ActivityTrackingType.YES_NO,
        isCompleted = true,
        category = "Reflection",
        lifeArea = "Mind",
        streakCount = 4,
        repeatText = "Hằng ngày"
    )
)

@Preview(
    name = "ActivitiesScreen - Light",
    showBackground = true
)
@Composable
private fun ActivitiesScreenLightPreview() {
    AppTheme(
        darkTheme = false,
        dynamicColor = false
    ) {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            ActivitiesScreen()
        }
    }
}

@Preview(
    name = "ActivitiesScreen - Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun ActivitiesScreenDarkPreview() {
    AppTheme(
        darkTheme = true,
        dynamicColor = false
    ) {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            ActivitiesScreen()
        }
    }
}