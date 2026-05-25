package com.almaslowcore.oasis.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.hilt.navigation.compose.hiltViewModel
import com.almaslowcore.oasis.features.activity.presentation.screen.ActivitiesScreen
import com.almaslowcore.oasis.features.activity.presentation.screen.ActivityDetailScreen
import com.almaslowcore.oasis.features.activity.presentation.screen.CreateActivityRoute
import com.almaslowcore.oasis.features.activity.presentation.screen.CreateActivityScreen
import com.almaslowcore.oasis.features.activity.presentation.screen.EditActivityScreen
import com.almaslowcore.oasis.features.gamification.presentation.screen.ProfileScreen
import com.almaslowcore.oasis.features.home.presentation.screen.HomeScreen
import com.almaslowcore.oasis.features.journal.presentation.screen.JournalScreen


@Composable
fun AppNavGraph(

    // Controller used to navigate between screens
    navController: NavHostController
) {

    NavHost(

        // Navigation controller
        navController = navController,

        // First screen shown when app starts
        startDestination = OasisDestination.Home.route
    ) {

        /*
         * composable(route)
         *
         * Defines a screen destination inside navigation graph.
         */

        composable(
            route = OasisDestination.Home.route
        ) {
            HomeScreen()
        }

        composable(
            route = OasisDestination.Activities.route
        ) {
            ActivitiesScreen(
                onCreateActivity = {
                    navController.navigate(OasisDestination.CreateActivity.route)
                },
                onActivityClick = { activityId ->
                    navController.navigate("activity_detail/$activityId")
                }
            )
        }

        composable(
            route = OasisDestination.Journal.route
        ) {
            JournalScreen()
        }

        composable(
            route = OasisDestination.Oasis.route
        ) {
            ProfileScreen()
        }

        composable(
            route = OasisDestination.CreateActivity.route
        ) {
            CreateActivityRoute(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = OasisDestination.ActivityDetail.route,
            arguments = listOf(
                navArgument("activityId") { type = NavType.StringType }
            )
        ) {
            ActivityDetailScreen()
        }

        composable(
            route = OasisDestination.EditActivity.route,
            arguments = listOf(
                navArgument("activityId") { type = NavType.StringType }
            )
        ) {
            EditActivityScreen()
        }
    }
}