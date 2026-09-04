package de.minmon.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import de.minmon.app.data.MinMonScreen
import de.minmon.app.ui.screens.dates.DatesScreen
import de.minmon.app.ui.screens.other.OtherScreen
import de.minmon.app.ui.screens.podcast.PodcastScreen
import de.minmon.app.ui.screens.record.RecordScreen
import de.minmon.app.util.Router
import de.minmon.design.components.snackbar.MMSnackbar
import de.minmon.design.token.RbSpacing
import de.minmon.app.ui.screens.home.HomeScreen
import de.minmon.app.ui.screens.post.PostScreen
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MinMonApp(
    windowSizeClass: WindowSizeClass,
    navController: NavHostController = rememberNavController()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val backStackEntry by navController.currentBackStackEntryAsState()
    val dismissSnackbarState = rememberSwipeToDismissBoxState(confirmValueChange = { value ->
        if (value != SwipeToDismissBoxValue.Settled) {
            snackbarHostState.currentSnackbarData?.dismiss()
            true
        } else {
            false
        }
    })

    LaunchedEffect("navigation") {
        Router.sharedFlow.onEach {
            when (it) {
                is Router.NavigationType.PopBack -> navController.popBackStack()
                is Router.NavigationType.PopUpTo -> navController.popBackStack(it.destinationId, it.inclusive)
                is Router.NavigationType.NavigateTo -> navController.navigate(it.target.route)
            }
        }.launchIn(this)
    }

    Scaffold(
        bottomBar = { AppBottomNavigation(navController = navController) },
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
            startDestination = MinMonScreen.Home.name,
            modifier = Modifier.padding(top = innerPadding.calculateTopPadding(), bottom = innerPadding.calculateBottomPadding()).background(
                MaterialTheme.colorScheme.background),

            ) {
            composable(route = MinMonScreen.Home.name) {
                HomeScreen(windowSizeClass)
            }
            composable(route = MinMonScreen.Dates.name) {
                DatesScreen(windowSizeClass)
            }
            composable(route = MinMonScreen.Podcast.name) {
                PodcastScreen(windowSizeClass)
            }
            composable(route = MinMonScreen.Other.name) {
                OtherScreen(windowSizeClass)
            }
            composable(route = MinMonScreen.Record.name) {
                RecordScreen(windowSizeClass)
            }
            composable(
                route = "${MinMonScreen.Post.name}/{postId}/{type}",
                arguments = listOf(
                    navArgument("postId") { type = NavType.IntType },
                    navArgument("type") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val postId = backStackEntry.arguments?.getInt("postId") ?: 0
                val isEvent = backStackEntry.arguments?.getString("type") == "event"
                PostScreen(windowSizeClass, postId, isEvent)
            }
        }
    }
}


@Composable
fun AppBottomNavigation(navController: NavController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Podcast,
        BottomNavItem.Dates,
        BottomNavItem.Record,
        BottomNavItem.Other,
    )
    NavigationBar()
    {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painterResource(id = item.icon),
                        contentDescription = item.title
                    )
                },
                label = { Text(text = item.title) },
                alwaysShowLabel = true,
                selected = currentRoute == item.screen_route,
                onClick = {
                    navController.navigate(item.screen_route) {

                        navController.graph.startDestinationRoute?.let { screen_route ->
                            popUpTo(screen_route) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
