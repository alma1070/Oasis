package com.almaslowcore.oasis.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.Spa
import com.almaslowcore.oasis.R

/*
 * Purpose:
 * This file defines all destinations (screens) in the app.
 *
 * Each destination contains a route string used by Navigation Compose.
 *
 * Think of this file as:
 * "The list of all places users can navigate to."
 */

sealed class OasisDestination(val route: String) {

    // data object = singleton object for Kotlin sealed class hierarchy
    data object Home : OasisDestination("home")
    data object Activities : OasisDestination("activities")
    data object Journal : OasisDestination("journal")
    data object Oasis : OasisDestination("oasis")


    data object FocusTimer : OasisDestination("focus_timer")
    data object ActivityDetail : OasisDestination("activity_detail/{activityId}")
    data object CreateActivity : OasisDestination("create_activity")
    data object AddJournal : OasisDestination("add_journal")
    data object Expense : OasisDestination("expense")
    data object LifeWheel : OasisDestination("life_wheel")
    data object VisionBoard : OasisDestination("vision_board")
}

/*
 * List of bottom navigation items.
 *
 * This makes BottomBar cleaner and avoids hardcoded UI values.
 */
val bottomNavItems = listOf(

    BottomNavItem(
        OasisDestination.Home.route,
        R.string.home,
        icon = Icons.Default.Home
    ),

    BottomNavItem(
        OasisDestination.Activities.route,
        R.string.activities,
        Icons.Default.CheckCircle
    ),

    BottomNavItem(
        OasisDestination.Journal.route,
        R.string.journal,
        Icons.Default.Mood
    ),

    BottomNavItem(
        OasisDestination.Oasis.route,
        R.string.app_name,
        Icons.Default.Spa
    )
)