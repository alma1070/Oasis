package com.almaslowcore.oasis.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.almaslowcore.oasis.ui.components.buttons.Fab
import com.almaslowcore.oasis.ui.navigation.BottomBarController
import com.almaslowcore.oasis.ui.navigation.LocalBottomBarController
import com.almaslowcore.oasis.ui.navigation.OasisBottomBar
import com.almaslowcore.oasis.ui.navigation.OasisBottomNavigation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold() {
    val navController = rememberNavController()
    val bottomBarController = remember {
        BottomBarController()
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
            }
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