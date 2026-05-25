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

    data object CreateActivity : OasisDestination(
        route = "create_activity",
        titleRes = R.string.create_activity,
        showBottomNav = false,
        isTopLevel = false
    )

    data object ActivityDetail : OasisDestination(
        route = "activity_detail/{activityId}",
        titleRes = R.string.activity_details,
        showBottomNav = false,
        isTopLevel = false
    )

    data object EditActivity : OasisDestination(
        route = "edit_activity/{activityId}",
        titleRes = R.string.edit_activity,
        showBottomNav = false,
        isTopLevel = false
    )
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
        OasisDestination.CreateActivity.route -> OasisDestination.CreateActivity
        OasisDestination.ActivityDetail.route -> OasisDestination.ActivityDetail
        OasisDestination.EditActivity.route -> OasisDestination.EditActivity
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