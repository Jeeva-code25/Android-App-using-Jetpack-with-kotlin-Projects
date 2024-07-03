package com.example.prepgenius

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation

fun NavGraphBuilder.beginningNavGraph(
    navController : NavHostController
){

    navigation(
        startDestination = Screens.LoginScreen.route,
        route = BEGIN
    ){

        composable(
            route = Screens.LoginScreen.route,
        ){
            LogInScreen(navController)
        }
        composable(
            route = Screens.RegisterScreen.route,
        ){
            RegistrationScreen(navController)
        }
    }
}