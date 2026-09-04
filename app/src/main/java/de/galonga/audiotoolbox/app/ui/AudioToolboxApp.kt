package de.galonga.audiotoolbox.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import de.galonga.audiotoolbox.app.R
import de.galonga.audiotoolbox.app.ui.nav.CalculatorDestinations
import de.galonga.audiotoolbox.app.ui.nav.TopLevelDestination
import de.galonga.audiotoolbox.app.ui.screens.analyzer.AnalyzerScreen
import de.galonga.audiotoolbox.app.ui.screens.dbcalculator.DbCalculatorScreen
import de.galonga.audiotoolbox.app.ui.screens.delaycalculator.DelayCalculatorScreen
import de.galonga.audiotoolbox.app.ui.screens.generator.SignalGeneratorScreen
import de.galonga.audiotoolbox.app.ui.screens.powerimpedance.PowerImpedanceCalculatorScreen
import de.galonga.audiotoolbox.app.ui.screens.record.RecordScreen
import de.galonga.audiotoolbox.app.ui.screens.settings.SettingsScreen
import de.galonga.audiotoolbox.app.ui.screens.tools.ToolsScreen
import de.galonga.audiotoolbox.design.components.snackbar.MMSnackbar
import de.galonga.audiotoolbox.design.token.RbSpacing

private data class BottomNavItem(val destination: TopLevelDestination, val labelRes: Int, val icon: ImageVector)

// Tools first/default, Record last — order matters for both the bottom bar and the start destination below.
private val bottomNavItems = listOf(
    BottomNavItem(TopLevelDestination.Tools, R.string.tools_screen, Icons.Default.Calculate),
    BottomNavItem(TopLevelDestination.Analyzer, R.string.analyzer_screen, Icons.Default.Equalizer),
    BottomNavItem(TopLevelDestination.Generator, R.string.generator_screen, Icons.Default.Waves),
    BottomNavItem(TopLevelDestination.Record, R.string.record_screen, Icons.Default.Mic),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioToolboxApp(
    windowSizeClass: WindowSizeClass,
    navController: NavHostController = rememberNavController()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val dismissSnackbarState = rememberSwipeToDismissBoxState(confirmValueChange = { value ->
        if (value != SwipeToDismissBoxValue.Settled) {
            snackbarHostState.currentSnackbarData?.dismiss()
            true
        } else {
            false
        }
    })

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = bottomNavItems.any { it.destination.route == currentRoute }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.destination.route,
                            onClick = {
                                navController.navigate(item.destination.route) {
                                    navController.graph.startDestinationRoute?.let { start ->
                                        popUpTo(start) { saveState = true }
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = stringResource(item.labelRes)) },
                            label = { Text(stringResource(item.labelRes)) }
                        )
                    }
                }
            }
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .padding(bottom = RbSpacing.space16)
            ) { data ->
                SwipeToDismissBox(
                    state = dismissSnackbarState,
                    backgroundContent = {},
                    content = {
                        MMSnackbar(
                            snackbarData = data
                        )
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = TopLevelDestination.Tools.route,
            modifier = Modifier
                .padding(top = innerPadding.calculateTopPadding(), bottom = innerPadding.calculateBottomPadding())
                .background(MaterialTheme.colorScheme.background),
        ) {
            composable(TopLevelDestination.Tools.route) {
                ToolsScreen(onNavigateToCalculator = { route -> navController.navigate(route) })
            }
            composable(TopLevelDestination.Analyzer.route) {
                AnalyzerScreen()
            }
            composable(TopLevelDestination.Generator.route) {
                SignalGeneratorScreen()
            }
            composable(TopLevelDestination.Record.route) {
                RecordScreen(windowSizeClass)
            }
            composable(CalculatorDestinations.DbCalculator) {
                DbCalculatorScreen(onBack = { navController.popBackStack() })
            }
            composable(CalculatorDestinations.DelayCalculator) {
                DelayCalculatorScreen(onBack = { navController.popBackStack() })
            }
            composable(CalculatorDestinations.PowerImpedanceCalculator) {
                PowerImpedanceCalculatorScreen(onBack = { navController.popBackStack() })
            }
            composable(CalculatorDestinations.Settings) {
                SettingsScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
