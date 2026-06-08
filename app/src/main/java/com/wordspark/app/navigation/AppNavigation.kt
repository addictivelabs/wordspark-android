package com.wordspark.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.wordspark.app.ui.home.HomeScreen
import com.wordspark.app.ui.puzzle.PuzzleScreen
import com.wordspark.app.ui.results.ResultsScreen

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Puzzle : Screen("puzzle")
    data object Results : Screen("results/{score}/{streak}") {
        fun createRoute(score: Int, streak: Int) = "results/$score/$streak"
    }
}

@Composable
fun WordSparkNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onPlayClicked = {
                    navController.navigate(Screen.Puzzle.route)
                }
            )
        }

        composable(Screen.Puzzle.route) {
            PuzzleScreen(
                onPuzzleComplete = { score, streak ->
                    navController.navigate(Screen.Results.createRoute(score, streak)) {
                        popUpTo(Screen.Puzzle.route) { inclusive = true }
                    }
                },
                onBackPressed = {
                    navController.popBackStack()
                }
            )
        }

        composable("results/{score}/{streak}") { backStackEntry ->
            val score = backStackEntry.arguments?.getString("score")?.toIntOrNull() ?: 0
            val streak = backStackEntry.arguments?.getString("streak")?.toIntOrNull() ?: 0
            ResultsScreen(
                score = score,
                streak = streak,
                onPlayAgain = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
