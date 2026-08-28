package com.adarshsingh.antidistraction.ui.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.adarshsingh.antidistraction.ui.alarms.AlarmsScreen
import com.adarshsingh.antidistraction.ui.alarms.AlarmsViewModel
import com.adarshsingh.antidistraction.ui.analytics.AnalyticsScreen
import com.adarshsingh.antidistraction.ui.analytics.AnalyticsViewModel
import com.adarshsingh.antidistraction.ui.apps.AppManagementScreen
import com.adarshsingh.antidistraction.ui.apps.AppManagementViewModel
import com.adarshsingh.antidistraction.ui.focus.FocusScreen
import com.adarshsingh.antidistraction.ui.focus.FocusViewModel
import com.adarshsingh.antidistraction.ui.rules.RulesScreen
import com.adarshsingh.antidistraction.ui.rules.RulesViewModel
import com.adarshsingh.antidistraction.ui.today.TodayScreen
import com.adarshsingh.antidistraction.ui.today.TodayViewModel

sealed class NavigationTab(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Focus : NavigationTab("focus", "Focus", Icons.Default.Home)
    object Today : NavigationTab("today", "Today", Icons.Default.DateRange)
    object Alarms : NavigationTab("alarms", "Alarms", Icons.Default.Notifications)
    object Apps : NavigationTab("apps", "Apps", Icons.Default.Menu)
    object Rules : NavigationTab("rules", "Rules", Icons.Default.Settings)
    object Analytics : NavigationTab("analytics", "Analytics", Icons.Default.Info)
}

val NAVIGATION_TABS = listOf(
    NavigationTab.Focus,
    NavigationTab.Today,
    NavigationTab.Alarms,
    NavigationTab.Apps,
    NavigationTab.Rules,
    NavigationTab.Analytics
)

@Composable
fun MainAppShell(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: NavigationTab.Focus.route

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                NAVIGATION_TABS.forEach { tab ->
                    val isSelected = currentRoute == tab.route
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            if (currentRoute != tab.route) {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = { Icon(imageVector = tab.icon, contentDescription = tab.title) },
                        label = { Text(text = tab.title, style = MaterialTheme.typography.labelSmall) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.tertiary,
                            unselectedTextColor = MaterialTheme.colorScheme.tertiary,
                            indicatorColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavigationTab.Focus.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavigationTab.Focus.route) {
                val focusViewModel: FocusViewModel = hiltViewModel()
                FocusScreen(viewModel = focusViewModel)
            }
            composable(NavigationTab.Today.route) {
                val todayViewModel: TodayViewModel = hiltViewModel()
                TodayScreen(viewModel = todayViewModel)
            }
            composable(NavigationTab.Alarms.route) {
                val alarmsViewModel: AlarmsViewModel = hiltViewModel()
                AlarmsScreen(viewModel = alarmsViewModel)
            }
            composable(NavigationTab.Apps.route) {
                val appViewModel: AppManagementViewModel = hiltViewModel()
                AppManagementScreen(viewModel = appViewModel)
            }
            composable(NavigationTab.Rules.route) {
                val rulesViewModel: RulesViewModel = hiltViewModel()
                RulesScreen(viewModel = rulesViewModel)
            }
            composable(NavigationTab.Analytics.route) {
                val analyticsViewModel: AnalyticsViewModel = hiltViewModel()
                AnalyticsScreen(viewModel = analyticsViewModel)
            }
        }
    }
}
