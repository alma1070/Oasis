package com.almaslowcore.oasis.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

/*
import com.almaslowcore.oasis.features.todo.presentation.TodoScreen
import com.almaslowcore.oasis.features.focus.presentation.FocusScreen
import com.almaslowcore.oasis.features.journal.presentation.JournalScreen
import com.almaslowcore.oasis.features.steps.presentation.StepsScreen
import com.almaslowcore.oasis.features.gamification.presentation.GamificationScreen
*/
/*
 * Purpose:
 * This file contains:
 * - NavHost
 * - Navigation Graph
 *
 * NavHost:
 * A container that displays the current screen.
 *
 * NavGraph:
 * The map of all routes and their corresponding screens.
 */

@Composable
fun AppNavGraph(

    // Controller used to navigate between screens
    navController: NavHostController
) {

    NavHost(

        // Navigation controller
        navController = navController,

        // First screen shown when app starts
        startDestination = OasisDestination.Todo.route
    ) {

        /*
         * composable(route)
         *
         * Defines a screen destination inside navigation graph.
         */

        composable(
            route = OasisDestination.Todo.route
        ) {
            //TodoScreen()
        }

        composable(
            route = OasisDestination.Focus.route
        ) {
            //FocusScreen()
        }

        composable(
            route = OasisDestination.Journal.route
        ) {
            //JournalScreen()
        }

        composable(
            route = OasisDestination.Steps.route
        ) {
            //StepsScreen()
        }

        composable(
            route = OasisDestination.Gamification.route
        ) {
            //GamificationScreen()
        }
    }
}