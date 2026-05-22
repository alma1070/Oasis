package com.almaslowcore.oasis.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Timer
import androidx.compose.ui.graphics.vector.ImageVector

/*
 * Purpose:
 * This file defines the data model for Bottom Navigation items.
 *
 * Each item contains:
 * - destination -> where to navigate
 * - label -> text shown in bottom bar
 * - icon -> icon shown in bottom bar
 */

data class BottomNavItem(

    // Screen destination
    val destination: OasisDestination,

    // Text label shown below icon
    val label: String,

    // Material icon
    val icon: ImageVector
)

/*
 * List of bottom navigation items.
 *
 * This makes BottomBar cleaner and avoids hardcoded UI values.
 */
val bottomNavItems = listOf(

    BottomNavItem(
        destination = OasisDestination.Todo,
        label = "Todo",
        icon = Icons.Default.CheckCircle
    ),

    BottomNavItem(
        destination = OasisDestination.Focus,
        label = "Focus",
        icon = Icons.Default.Timer
    ),

    BottomNavItem(
        destination = OasisDestination.Journal,
        label = "Journal",
        icon = Icons.Default.Edit
    ),

    BottomNavItem(
        destination = OasisDestination.Steps,
        label = "Steps",
        icon = Icons.Default.DirectionsWalk
    ),

    BottomNavItem(
        destination = OasisDestination.Gamification,
        label = "Rewards",
        icon = Icons.Default.EmojiEvents
    )
)