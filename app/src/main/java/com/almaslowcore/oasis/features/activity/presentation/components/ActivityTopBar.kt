package com.almaslowcore.oasis.features.activity.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.GroupWork
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.almaslowcore.oasis.R
import com.almaslowcore.oasis.ui.components.topbar.OasisDateTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityTopBar(
    title: String,
    subtitle: String,
    onPreviousClick: () -> Unit, // Added
    onNextClick: () -> Unit,
    onFilterClick: () -> Unit,
    onGroupByClick: () -> Unit,
    onDatePickerClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OasisDateTopBar(
        title = title,
        subtitle = subtitle,
        onPreviousClick = onPreviousClick,
        onNextClick = onNextClick,
        onTitleClick = onDatePickerClick,
        actions = {
            IconButton(onClick = onFilterClick) {
                Icon(Icons.Outlined.FilterList, "Filter")
            }
            IconButton(onClick = onGroupByClick) {
                Icon(Icons.Outlined.GroupWork, "Group")
            }
        }
    )
}

@Composable
private fun RowScope.ActivityTopBarActions(
    onFilterClick: () -> Unit,
    onGroupByClick: () -> Unit,
    onDatePickerClick: () -> Unit
) {
    IconButton(
        onClick = onFilterClick
    ) {
        Icon(
            imageVector = Icons.Outlined.FilterList,
            contentDescription = stringResource(R.string.filter_activities)
        )
    }

    IconButton(
        onClick = onGroupByClick
    ) {
        Icon(
            imageVector = Icons.Outlined.GroupWork,
            contentDescription = stringResource(R.string.group_activities)
        )
    }

    IconButton(
        onClick = onDatePickerClick
    ) {
        Icon(
            imageVector = Icons.Outlined.CalendarMonth,
            contentDescription = stringResource(R.string.choose_date)
        )
    }
}