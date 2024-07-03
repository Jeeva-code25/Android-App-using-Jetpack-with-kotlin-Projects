package com.example.prepgenius

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.navigation

@Composable
fun RootNavHost(
    navController : NavHostController
) {
    val context = LocalContext.current

    val sharedPreference = context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE)
    val isSigned by remember { mutableStateOf(sharedPreference.getBoolean("USER_SIGNED",false)) }

    NavHost(navController = navController,
        startDestination = if (!isSigned) BEGIN else MIDDLE,
        route = ROOT){

        beginningNavGraph(navController = navController,)
        middleNavGraph(navController = navController)

    }
}