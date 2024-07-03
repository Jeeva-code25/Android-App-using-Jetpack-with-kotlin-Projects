package com.example.prepgenius

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.prepgenius.ui.theme.Roboto
import com.example.prepgenius.ui.theme.primColor

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    navController : NavHostController,
) {
    val context = LocalContext.current

    val sharedPreference = context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE)
    val sharedEdit = sharedPreference.edit()

    val username by remember { mutableStateOf(sharedPreference.getString("USERNAME","")) }
    val phone by remember { mutableStateOf(sharedPreference.getString("PHONE","")) }

    Scaffold (
        modifier = Modifier.fillMaxSize(),
        topBar = { TopBar( username = username.toString()) },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier
                    .size(50.dp),
                shape = RoundedCornerShape(8.dp),
                onClick = {
                    sharedEdit.clear().apply()
                    navController.navigate(route = BEGIN){
                        popUpTo(MIDDLE){
                            inclusive = true
                        }
                    }
                },
                containerColor = Color(0xFFABD1C6),
                contentColor = primColor
            ) {

                Icon(imageVector = Icons.Outlined.Logout, contentDescription = "logout")

            }
        },
        floatingActionButtonPosition = FabPosition.End,
        containerColor = primColor,
        content = {

            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = it.calculateTopPadding(),
                        bottom = it.calculateBottomPadding()
                    ),

            ) {

                Column(
                    modifier = Modifier
                        .verticalScroll(state = rememberScrollState())
                        .fillMaxSize()
                        .background(color = primColor)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Image(
                        modifier = Modifier
                            .height(250.dp)
                            .width(300.dp),
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo",
                        contentScale = ContentScale.FillBounds
                    )

                    Button(
                        modifier = Modifier
                            .height(55.dp)
                            .width(280.dp)
                            .clip(shape = RoundedCornerShape(20.dp)),
                        onClick = {
                                  navController.navigate(Screens.InstructionScreen.route + "/${"NEET"}")
                                  },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF79b2e2),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "NEET",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(25.dp))
                    Button(
                        modifier = Modifier
                            .height(55.dp)
                            .width(280.dp)
                            .clip(shape = RoundedCornerShape(20.dp)),
                        onClick = {
                            navController.navigate(Screens.InstructionScreen.route + "/${"JEE"}")

                        },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF2400),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "JEE",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(25.dp))
                    Button(
                        modifier = Modifier
                            .height(55.dp)
                            .width(280.dp)
                            .clip(shape = RoundedCornerShape(20.dp)),
                        onClick = {
                            navController.navigate(Screens.SpecificSubjectsScreen.route)
                                  },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF8C660),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Specific Subjects",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(25.dp))

                    if (phone == "+911234567890") {
                        Button(
                            modifier = Modifier
                                .height(55.dp)
                                .width(280.dp)
                                .clip(shape = RoundedCornerShape(20.dp)),
                            onClick = {
                                navController.navigate(Screens.QuestionUploadScreen.route)
                            },
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF097969),
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = "Upload Questions",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(40.dp))





                }
            }
        }
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    username : String = "username"
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    TopAppBar(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        title = {
            Text(
                text = "Hi, $username \nLet's start your preparation",
                fontFamily = Roboto,
                fontSize = 26.sp,
                fontStyle = FontStyle.Italic,
                color = Color.White
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = primColor,
            titleContentColor = Color.White
        )
    )

}

