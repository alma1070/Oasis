package com.almaslowcore.oasis.core.navigation

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
    val route: String,

    // Text label shown below icon
    val labelRes: Int,

    // Material icon
    val icon: ImageVector
)

