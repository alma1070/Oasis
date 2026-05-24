package com.almaslowcore.oasis.core.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.almaslowcore.oasis.ui.components.OasisBottomNavigation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Find the current destination object to get the title
    val currentDestination = remember(currentRoute) {
        getDestinationFromRoute(currentRoute)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(currentDestination?.titleRes?.let { stringResource(it) } ?: "")
                },
                actions = {
                    if (currentDestination?.isTopLevel == true) {
                        // Core Action Items (e.g., Profile, Notifications)
                        IconButton(onClick = { /* TODO */ }) {
                            Icon(Icons.Default.Person, contentDescription = null)
                        }
                    } else {
                        // Detail/Form Action Items (e.g., Save, Delete)
                        IconButton(onClick = { /* TODO */ }) {
                            Icon(Icons.Default.Done, contentDescription = null)
                        }
                    }
                },
                navigationIcon = {
                    if (currentDestination?.isTopLevel == false) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                }
            )
        },
        bottomBar = {
            // Logic: Only show the main BottomNav on Top Level screens
            if (currentDestination?.showBottomNav == true) {
                OasisBottomNavigation(navController, currentRoute)
            } else if (currentDestination?.isTopLevel == false) {
                // Logic: Show a different BottomAppBar for forms/details (e.g., "Apply" button)
                BottomAppBar {
                    Button(onClick = { /* Action */ }, modifier = Modifier.fillMaxWidth()) {
                        Text("Submit")
                    }
                }
            }
        }
    ) { innerPadding ->

        // Use a simple Box, NO second Scaffold here
        Box(
            modifier = Modifier.padding(innerPadding)
        ) {
            AppNavGraph(navController = navController)
        }
    }
}