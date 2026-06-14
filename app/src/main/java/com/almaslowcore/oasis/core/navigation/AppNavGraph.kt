package com.almaslowcore.oasis.core.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.almaslowcore.oasis.features.activity.presentation.screen.ActivityScreen
import com.almaslowcore.oasis.features.activity.presentation.screen.CreateActivityRoute
import com.almaslowcore.oasis.features.gamification.presentation.screen.ProfileScreen
import com.almaslowcore.oasis.features.home.presentation.screen.HomeRoute
import com.almaslowcore.oasis.features.home.presentation.screen.HomeScreen
import com.almaslowcore.oasis.features.journal.presentation.screen.JournalEntryFormScreen
import com.almaslowcore.oasis.features.journal.presentation.screen.JournalScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = OasisDestination.Home.route,
        enterTransition = {
            val initial = getRoutePriority(initialState.destination.route)
            val target = getRoutePriority(targetState.destination.route)

            // If target priority is higher (further right), slide Left (forward)
            val direction = if (target > initial) {
                AnimatedContentTransitionScope.SlideDirection.Left
            } else {
                AnimatedContentTransitionScope.SlideDirection.Right
            }

            slideIntoContainer(towards = direction, animationSpec = tween(300)) +
                    fadeIn(animationSpec = tween(300))
        },
        exitTransition = {
            val initial = getRoutePriority(initialState.destination.route)
            val target = getRoutePriority(targetState.destination.route)

            val direction = if (target > initial) {
                AnimatedContentTransitionScope.SlideDirection.Left
            } else {
                AnimatedContentTransitionScope.SlideDirection.Right
            }

            slideOutOfContainer(towards = direction, animationSpec = tween(300)) +
                    fadeOut(animationSpec = tween(300))
        },
        popEnterTransition = {
            // Secondary screens back: Left to Right
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300))
        },
        popExitTransition = {
            // Secondary screens back: Left to Right
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        }
    ) {
        composable(route = OasisDestination.Home.route) {
            HomeRoute()
        }

        composable(route = OasisDestination.Activities.route) {
            ActivityScreen(
                onNavigateToCreate = {
                    navController.navigate(OasisDestination.CreateActivity.route)
                }
            ) }

        composable(route = OasisDestination.Journal.route) {
            JournalScreen(
                onNavigateToCreateJournal = { navController.navigate(OasisDestination.JournalCheckIn.route) },
                onNavigateToEditJournal = { entryId ->
                    navController.navigate(OasisDestination.JournalEdit.createRoute(entryId))
                }
            )
        }

        composable(route = OasisDestination.Oasis.route) { ProfileScreen() }

        composable(route = OasisDestination.CreateActivity.route) {
            CreateActivityRoute(onNavigateBack = { navController.popBackStack() })
        }


        composable(route = OasisDestination.JournalCheckIn.route) {
            JournalEntryFormScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(
            route = OasisDestination.JournalEdit.route,
            arguments = listOf(navArgument(OasisDestination.JournalEdit.ENTRY_ID_ARG) { type = NavType.LongType })
        ) { JournalEntryFormScreen(onNavigateBack = { navController.popBackStack() }) }

    }
}

/**
 * Assigns a numeric priority to routes to determine slide direction.
 * Top Level (Bottom Nav): 0 to 3
 * Secondary screens: 10 (always slide right-to-left when opening)
 */
private fun getRoutePriority(route: String?): Int {
    return when (route) {
        OasisDestination.Home.route -> 0
        OasisDestination.Activities.route -> 1
        OasisDestination.Journal.route -> 2
        OasisDestination.Oasis.route -> 3
        // Any other screen (Create, Edit, Detail) is priority 10
        else -> 10
    }
}