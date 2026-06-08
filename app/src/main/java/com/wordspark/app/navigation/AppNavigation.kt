package com.wordspark.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.wordspark.app.ui.home.HomeScreen
import com.wordspark.app.ui.puzzle.PuzzleScreen
import com.wordspark.app.ui.results.ResultsScreen

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Puzzle : Screen("puzzle")
    data object Results : Screen("results/{score}") {
        fun createRoute(score: Int) = "results/$score"
    }
}

@Composable
fun WordSparkNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onPlayClick = { navController.navigate(Screen.Puzzle.route) }
            )
        }

        composable(Screen.Puzzle.route) {
            PuzzleScreen(
                onPuzzleComplete = { score ->
                    navController.navigate(Screen.Results.createRoute(score)) {
                        popUpTo(Screen.Puzzle.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Results.route,
            arguments = listOf(navArgument("score") { type = NavType.IntType })
        ) { backStackEntry ->
            val score = backStackEntry.arguments?.getInt("score") ?: 0
            ResultsScreen(
                score = score,
                onPlayAgain = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
