package com.almaslowcore.oasis.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.almaslowcore.oasis.ui.components.OasisBottomNavigation

/*
 * Purpose:
 * This file creates the main app layout.
 *
 * Responsibilities:
 * - Create NavController
 * - Display Bottom Navigation Bar
 * - Display NavHost
 */

@Composable
fun MainScaffold() {
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            OasisBottomNavigation(
                navController = navController,
                currentRoute = currentRoute
            )
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier.padding(innerPadding)
        ) {
            AppNavGraph(navController = navController)
        }
    }
}