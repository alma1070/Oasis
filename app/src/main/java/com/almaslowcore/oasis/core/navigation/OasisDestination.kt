package com.almaslowcore.oasis.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.Spa
import androidx.compose.ui.graphics.vector.ImageVector
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

sealed class OasisDestination(
    val route: String,
    val icon: ImageVector? = null,
    val titleRes: Int? = null,
    val labelRes: Int? = null,
    val showBottomNav: Boolean = true,
    val isTopLevel: Boolean = true) {

    // data object = singleton object for Kotlin sealed class hierarchy
    data object Home : OasisDestination(
        "home",
        Icons.Default.Home,
        labelRes = R.string.home)
    data object Activities : OasisDestination(
        "activities",
        Icons.Default.CheckCircle,
        labelRes = R.string.activities)
    data object Journal : OasisDestination(
        "journal",
        Icons.Default.Mood,
        labelRes = R.string.journal)
    data object Oasis : OasisDestination(
        "oasis",
        Icons.Default.Spa,
        labelRes = R.string.app_name)


    /*
    // Example of a Detail screen
    data object TaskDetail : OasisDestination(
        route = "task_detail/{taskId}",
        titleRes = R.string.task_details,
        showBottomNav = false, // Detail screens often hide the main bottom nav
        isTopLevel = false
    )
    data object FocusTimer : OasisDestination("focus_timer")
    data object ActivityDetail : OasisDestination("activity_detail/{activityId}")
    data object CreateActivity : OasisDestination("create_activity")
    data object AddJournal : OasisDestination("add_journal")
    data object Expense : OasisDestination("expense")
    data object LifeWheel : OasisDestination("life_wheel")
    data object VisionBoard : OasisDestination("vision_board")

     */
}

/**
 * Maps a route string back to an OasisDestination object.
 */
fun getDestinationFromRoute(route: String?): OasisDestination? {
    return when (route?.substringBefore("/")) { // substringBefore handles routes with arguments like "detail/{id}"
        OasisDestination.Home.route -> OasisDestination.Home
        OasisDestination.Activities.route -> OasisDestination.Activities
        OasisDestination.Journal.route -> OasisDestination.Journal
        OasisDestination.Oasis.route -> OasisDestination.Oasis
        else -> null
    }
}

/*
 * List of bottom navigation items.
 *
 * This makes BottomBar cleaner and avoids hardcoded UI values.
 */
val bottomNavItems = listOf(
    OasisDestination.Home,
    OasisDestination.Activities,
    OasisDestination.Journal,
    OasisDestination.Oasis
)