package com.almaslowcore.oasis.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.almaslowcore.oasis.core.presentation.MainViewModel
import com.almaslowcore.oasis.ui.navigation.BottomBarController
import com.almaslowcore.oasis.ui.navigation.LocalBottomBarController
import com.almaslowcore.oasis.ui.navigation.OasisBottomBar
import com.almaslowcore.oasis.ui.navigation.OasisBottomNavigation
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    viewModel: MainViewModel = viewModel()
) {
    val navController = rememberNavController()
    val bottomBarController = remember {
        BottomBarController()
    }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.rewardEvents.collectLatest { event ->
            snackbarHostState.showSnackbar(
                message = "+${event.xpAmount} XP: ${event.reason}"
            )
        }
    }

    LaunchedEffect(Unit) {
        viewModel.levelUpEvents.collectLatest { newLevel ->
            snackbarHostState.showSnackbar(
                message = "LEVEL UP! You are now level $newLevel 🎉"
            )
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val currentDestination = remember(currentRoute) {
        getDestinationFromRoute(currentRoute)
    }

    CompositionLocalProvider(
        LocalBottomBarController provides bottomBarController
    ) {
        Scaffold(
            topBar = {
                if (currentDestination?.isTopLevel == false) {
                    TopAppBar(
                        title = {
                            Text(
                                currentDestination?.titleRes?.let {
                                    stringResource(it)
                                } ?: ""
                            )
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = {
                                    navController.popBackStack()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        }
                    )
                }
            },
            bottomBar = {
                if (currentDestination?.showBottomNav == true) {
                    OasisBottomNavigation(
                        navController = navController,
                        currentRoute = currentRoute
                    )
                } else {
                    bottomBarController.config?.let { config ->
                        OasisBottomBar(config = config)
                    }
                }
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { innerPadding ->
            Box(
                modifier = Modifier.padding(innerPadding)
            ) {
                AppNavGraph(
                    navController = navController
                )
            }
        }
    }
}
