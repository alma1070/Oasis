package com.almaslowcore.oasis.core.navigation

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
    data object Todo : OasisDestination("todo")

    data object Focus : OasisDestination("focus")

    data object Journal : OasisDestination("journal")

    data object Steps : OasisDestination("steps")

    data object Gamification : OasisDestination("gamification")
}