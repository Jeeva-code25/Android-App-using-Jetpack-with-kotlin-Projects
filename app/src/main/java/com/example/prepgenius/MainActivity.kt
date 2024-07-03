package com.example.prepgenius

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.prepgenius.ui.theme.PrepGeniusTheme
import com.example.prepgenius.ui.theme.Roboto

import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PrepGeniusTheme {
                // A surface container using the 'background' color from the theme
                PrepGeniusStart(navController = rememberNavController())
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PrepGeniusStart(
    navController : NavHostController
) {
    val context = LocalContext.current
    var isVisible by remember { mutableStateOf(false) }

    if (isVisible && networkConnection(context = context)) {
        LaunchedEffect(isVisible && networkConnection(context = context)) {
            delay(3000)
            isVisible = false
        }
    }
    val alpha: Float by animateFloatAsState(
        targetValue = 1f ,
        animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing), label = "tween"
    )

    Scaffold {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = it.calculateTopPadding(), bottom = it.calculateBottomPadding())

        ) {

            RootNavHost(navController = navController)
        }
    }
    if (isVisible && networkConnection(context = context)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(25.dp)
                .background(color = Color(0xFF53B175))
                .alpha(alpha),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Back Online",
                color = Color.White,
                fontFamily = Roboto
            )
        }
    }
    if (!networkConnection(context = context)) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(25.dp)
                .background(color = Color.Black)
                .alpha(alpha),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text =  "No Connection",
                color = Color.White,
                fontFamily = Roboto
            )
        }
        isVisible = true

    }


}
