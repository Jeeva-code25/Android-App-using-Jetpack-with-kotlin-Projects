package com.example.prepgenius

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation

fun NavGraphBuilder.middleNavGraph(
    navController : NavHostController
){
    navigation(
        startDestination = Screens.HomeScreen.route,
        route = MIDDLE
    ){

        composable(
            route = Screens.HomeScreen.route
        ){
            HomeScreen(navController = navController)
        }

        composable(
            route = Screens.InstructionScreen.route+ "/{TEST_TYPE}"
        ){navBackStack->
            val testType = navBackStack.arguments?.getString("TEST_TYPE")
            InstructionScreen(navController = navController, testType = testType.toString())
        }
        composable(
            route = Screens.TestScreen.route+ "/{TEST_TYPE}/{TOTAL_QUESTION}"
        ){navBackStack->
            val testType = navBackStack.arguments?.getString("TEST_TYPE")
            val totalQuestion = navBackStack.arguments?.getString("TOTAL_QUESTION")

            TestScreen(navController = navController, testType = testType.toString(), totalQuestion = totalQuestion.toString())
        }

        composable(
            route = Screens.SpecificSubjectsScreen.route
        ){
            SpecificSubjectsScreen(navController = navController)
        }
        composable(
            route = Screens.QuestionUploadScreen.route
        ){
            UploadQuestions(navController = navController)
        }

        composable(
            route = Screens.ScorecardScreen.route+ "/{TOTAL_QUESTION}/{SCORE}/{CORRECT}/{WRONG}/{TEST_TYPE}"
        ){navBackStack->

            val totalQuestion = navBackStack.arguments?.getString("TOTAL_QUESTION")
            val score = navBackStack.arguments?.getString("SCORE")
            val correct = navBackStack.arguments?.getString("CORRECT")
            val wrong = navBackStack.arguments?.getString("WRONG")
            val testType = navBackStack.arguments?.getString("TEST_TYPE")

            ScorecardScreen(
                navController = navController,
                total = totalQuestion.toString(),
                score = score.toString(),
                correctAns = correct.toString(),
                wrongAns = wrong.toString(),
                testType = testType.toString()
            )
        }

    }
}