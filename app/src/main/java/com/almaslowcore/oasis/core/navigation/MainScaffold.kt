package com.almaslowcore.oasis.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

/*
 * Purpose:
 * This file creates the main app layout.
 *
 * Responsibilities:
 * - Create NavController
 * - Display Bottom Navigation Bar
 * - Display NavHost
 *
 * Scaffold:
 * A Material Design layout structure.
 *
 * It provides slots like:
 * - topBar
 * - bottomBar
 * - floatingActionButton
 */

@Preview
@Composable
fun MainScaffold() {

    /*
     * rememberNavController()
     *
     * Creates and remembers navigation controller.
     *
     * This controller manages:
     * - screen navigation
     * - back stack
     * - navigation state
     */
    val navController = rememberNavController()

    /*
     * currentBackStackEntryAsState()
     *
     * Observes current navigation destination.
     *
     * Used to highlight selected bottom navigation item.
     */
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    // Current route string
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(

        /*
         * Bottom navigation UI
         */
        bottomBar = {

            NavigationBar {

                // Loop through all bottom navigation items
                bottomNavItems.forEach { item ->

                    NavigationBarItem(

                        /*
                         * selected:
                         * Determines whether item is currently active.
                         */
                        selected = currentRoute == item.destination.route,

                        /*
                         * onClick:
                         * Navigate to destination when item clicked.
                         */
                        onClick = {

                            navController.navigate(item.destination.route) {

                                /*
                                 * Prevent multiple copies of same destination
                                 * in back stack.
                                 */
                                launchSingleTop = true

                                /*
                                 * Restore previous state if destination exists.
                                 */
                                restoreState = true

                                /*
                                 * Keeps bottom navigation state stable.
                                 */
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                            }
                        },

                        /*
                         * Icon shown in bottom navigation item
                         */
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label
                            )
                        },

                        /*
                         * Label shown below icon
                         */
                        label = {
                            Text(text = item.label)
                        }
                    )
                }
            }
        }

    ) { innerPadding ->

        /*
         * Box used to apply Scaffold padding.
         *
         * Without this padding,
         * content may overlap bottom navigation bar.
         */
        Box(
            modifier = Modifier.padding(innerPadding)
        ) {

            // Navigation host
            AppNavGraph(navController = navController)
        }
    }
}