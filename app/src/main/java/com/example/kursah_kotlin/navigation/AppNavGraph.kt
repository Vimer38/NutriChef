package com.example.kursah_kotlin.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.kursah_kotlin.screens.AuthScreen
import com.example.kursah_kotlin.screens.HomeScreen
import com.example.kursah_kotlin.screens.RecipeDetailScreen
import com.example.kursah_kotlin.screens.SavedRecipesScreen
import com.example.kursah_kotlin.screens.UserInfoScreen
import com.example.kursah_kotlin.screens.WelcomeScreen

object Destinations {
    const val Auth = "auth"
    const val Welcome = "welcome"
    const val UserInfo = "user_info"
    const val Home = "home"
    const val Saved = "saved"
    const val RecipeDetail = "recipe_detail"
    const val RecipeDetailRoute = "recipe_detail/{recipeId}"

    fun recipeDetailRoute(recipeId: String) = "$RecipeDetail/$recipeId"
}

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Destinations.Auth
    ) {
        composable(Destinations.Auth) {
            AuthScreen(
                onContinue = {
                    navController.navigate(Destinations.Welcome) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Destinations.Welcome) {
            WelcomeScreen(
                onSkipClick = {
                    navController.navigate(Destinations.Home) {
                        popUpTo(Destinations.Auth) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onGoalSelected = {
                    navController.navigate(Destinations.UserInfo) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Destinations.UserInfo) {
            UserInfoScreen(
                onSkipClick = {
                    navController.navigate(Destinations.Home) {
                        popUpTo(Destinations.Auth) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNextClick = {
                    navController.navigate(Destinations.Home) {
                        popUpTo(Destinations.Auth) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Destinations.Home) {
            HomeScreen(
                onSearchClick = { /* placeholder search action */ },
                onRecipeClick = { recipeId ->
                    navController.navigateSingleTop(Destinations.recipeDetailRoute(recipeId))
                },
                onSeeAllClick = { /* TODO */ },
                onNavigationClick = { handleBottomNav(it, navController) }
            )
        }

        composable(Destinations.Saved) {
            SavedRecipesScreen(
                onBackClick = { navController.popBackStack() },
                onRecipeClick = { recipeId ->
                    navController.navigateSingleTop(Destinations.recipeDetailRoute(recipeId))
                },
                onViewAllClick = { /* TODO */ },
                onNavigationClick = { handleBottomNav(it, navController) }
            )
        }

        composable(
            route = Destinations.RecipeDetailRoute,
            arguments = listOf(
                navArgument("recipeId") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) {
            RecipeDetailScreen(
                onBackClick = { navController.popBackStack() },
                onNavigationClick = { handleBottomNav(it, navController) },
                recipeId = it.arguments?.getString("recipeId") ?: ""
            )
        }
    }
}

private fun handleBottomNav(tab: String, navController: NavHostController) {
    when (tab) {
        "home" -> navController.navigateSingleTop(Destinations.Home)
        "search" -> navController.navigateSingleTop(Destinations.Home)
        "bookmark" -> navController.navigateSingleTop(Destinations.Saved)
        "profile" -> navController.navigateSingleTop(Destinations.UserInfo)
    }
}

private fun NavHostController.navigateSingleTop(route: String) {
    if (currentDestination?.route == route) return
    navigate(route) {
        launchSingleTop = true
    }
}

