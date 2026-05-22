package com.almaslowcore.oasis.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.almaslowcore.oasis.core.navigation.OasisDestination
import com.almaslowcore.oasis.core.navigation.bottomNavItems
import com.almaslowcore.oasis.ui.theme.AppTheme

/*
 * Purpose:
 * This file defines the custom bottom navigation bar for Oasis.
 *
 * It receives:
 * - navController: used to navigate between screens
 * - currentRoute: used to highlight the selected item
 */


@Composable
fun OasisBottomNavigation(
    navController: NavHostController,
    currentRoute: String?
) {
    NavigationBar(
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 10.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surface),

        containerColor =
            MaterialTheme.colorScheme.surfaceContainerLow,

        tonalElevation = 4.dp

    ) {
        bottomNavItems.forEach { item ->

            NavigationBarItem(
                selected = currentRoute == item.route,

                onClick = {
                    navController.navigate(item.route) {
                        launchSingleTop = true
                        restoreState = true

                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                    }
                },

                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = stringResource(item.labelRes)
                    )
                },

                label = {
                    Text(
                        text = stringResource(item.labelRes),
                        style = MaterialTheme.typography.labelMedium
                    )
                },

                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,

                )

            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OasisBottomNavigationPreview() {

    AppTheme {

        val navController = rememberNavController()

        OasisBottomNavigation(
            navController = navController,
            currentRoute = OasisDestination.Home.route
        )
    }
}