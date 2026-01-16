package com.example.hungerkiller

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.hungerkiller.ui.navigation.HungerKillerNavHost
import com.example.hungerkiller.ui.navigation.Screen
import com.example.hungerkiller.ui.theme.HungerKillerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { HungerKillerTheme { HungerKillerApp() } }
    }
}

@Composable
fun HungerKillerApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    // Determine which tab should be selected based on current route
    val currentDestination = when {
        currentRoute?.startsWith("home") == true -> AppDestinations.HOME
        currentRoute?.startsWith("search") == true -> AppDestinations.SEARCH
        currentRoute?.startsWith("favorites") == true -> AppDestinations.FAVORITES
        else -> AppDestinations.SEARCH
    }
    
    // Check if we're on a detail screen to hide bottom navigation
    val showBottomBar = currentRoute != null && !currentRoute.startsWith("recipe/")

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            if (showBottomBar) {
                AppDestinations.entries.forEach { destination ->
                    item(
                        icon = { Icon(destination.icon, contentDescription = destination.label) },
                        label = { Text(destination.label) },
                        selected = destination == currentDestination,
                        onClick = {
                            when (destination) {
                                AppDestinations.HOME -> navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Home.route) { inclusive = true }
                                    launchSingleTop = true
                                }
                                AppDestinations.SEARCH -> navController.navigate(Screen.Search.route) {
                                    popUpTo(Screen.Search.route) { inclusive = true }
                                    launchSingleTop = true
                                }
                                AppDestinations.FAVORITES -> navController.navigate(Screen.Favorites.route) {
                                    popUpTo(Screen.Favorites.route) { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        }
                    )
                }
            }
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize().padding(if (showBottomBar) 15.dp else 0.dp)
        ) { innerPadding ->
            HungerKillerNavHost(
                navController = navController,
                startDestination = Screen.Search.route,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
) {
    HOME("Home", Icons.Default.Home),
    SEARCH("Search", Icons.Default.Search),
    FAVORITES("Favorites", Icons.Default.Favorite),
}

