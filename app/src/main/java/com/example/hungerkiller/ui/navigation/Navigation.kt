package com.example.hungerkiller.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.hungerkiller.ui.screens.detail.RecipeDetailScreen
import com.example.hungerkiller.ui.screens.favorites.FavoritesScreen
import com.example.hungerkiller.ui.screens.home.HomeScreen
import com.example.hungerkiller.ui.screens.search.SearchScreen

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Search : Screen("search")
    data object Favorites : Screen("favorites")
    data object RecipeDetail : Screen("recipe/{recipeId}") {
        fun createRoute(recipeId: String) = "recipe/$recipeId"
    }
}

@Composable
fun HungerKillerNavHost(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onRecipeClick = { recipeId ->
                    navController.navigate(Screen.RecipeDetail.createRoute(recipeId))
                },
                onNavigateToSearch = {
                    navController.navigate(Screen.Search.route) {
                        popUpTo(Screen.Home.route)
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.Search.route) {
            SearchScreen(
                onRecipeClick = { recipeId ->
                    navController.navigate(Screen.RecipeDetail.createRoute(recipeId))
                }
            )
        }

        composable(Screen.Favorites.route) {
            FavoritesScreen(
                onRecipeClick = { recipeId ->
                    navController.navigate(Screen.RecipeDetail.createRoute(recipeId))
                }
            )
        }

        composable(
            route = Screen.RecipeDetail.route,
            arguments = listOf(
                navArgument("recipeId") { type = NavType.StringType }
            )
        ) {
            RecipeDetailScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
