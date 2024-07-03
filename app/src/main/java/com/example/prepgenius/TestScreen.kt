package com.example.prepgenius

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.prepgenius.ui.theme.Roboto
import com.example.prepgenius.ui.theme.primColor
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.delay

var SCORE_CARD_LIST : List<ScoreCardDataModel> = emptyList()

@Composable
fun TestScreen(
    navController : NavHostController,
    testType : String = "",
    totalQuestion: String = ""
) {

    val context = LocalContext.current

    val sharedPreference = context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE)
    val database = FirebaseDatabase.getInstance().getReference("Prep Genius")
        .child("Users")
        .child(sharedPreference.getString("PHONE","").toString())
        .child("Attempted Questions")

    val questionList = TOTAL_TEST_QUESTIONS
    var currentQuestion by remember {mutableStateOf(0)}
    val options = mutableListOf(
        if (currentQuestion < questionList.size) questionList[currentQuestion].option1 else questionList[questionList.size -1].option1,
        if (currentQuestion < questionList.size) questionList[currentQuestion].option2 else questionList[questionList.size -1].option2,
        if (currentQuestion < questionList.size) questionList[currentQuestion].option3 else questionList[questionList.size -1].option3,
        if (currentQuestion < questionList.size) questionList[currentQuestion].option4 else questionList[questionList.size -1].option4

    )

    var showProgress by remember { mutableStateOf(false) }

    var selectedOption by remember{ mutableStateOf("") }

    // create variable for current time
    var currentTime by remember { mutableStateOf(60) }
    // create variable for isTimerRunning
    var isTimerRunning by remember { mutableStateOf(true) }

    isTimerRunning = !showProgress

    var score by remember { mutableStateOf(0) }
    var correct by remember { mutableStateOf(0) }
    var wrong by remember { mutableStateOf(0) }


    val scoreCardList =  remember { mutableListOf<ScoreCardDataModel>() }



    if (currentTime<=0){

        if (selectedOption.isNotEmpty() && currentQuestion < questionList.size) {

            showProgress = true

            val selectedData = ScoreCardDataModel(
                id = questionList[currentQuestion].id,
                question = questionList[currentQuestion].question,
                option1 = questionList[currentQuestion].option1,
                option2 = questionList[currentQuestion].option2,
                option3 = questionList[currentQuestion].option3,
                option4 = questionList[currentQuestion].option4,
                answer = questionList[currentQuestion].answer,
                selected = selectedOption
            )
            if (selectedOption == questionList[currentQuestion].answer) {
                score += 2
                correct += 1
                scoreCardList.add(selectedData)
                database
                    .child(selectedData.id)
                    .child("id")
                    .setValue(selectedData.id)
                    .addOnSuccessListener {

                        showProgress = false
                    }
            } else {
                wrong += 1
                scoreCardList.add(selectedData)
            }

        } else {

            val selectedData = ScoreCardDataModel(
                id = questionList[currentQuestion].id,
                question = questionList[currentQuestion].question,
                option1 = questionList[currentQuestion].option1,
                option2 = questionList[currentQuestion].option2,
                option3 = questionList[currentQuestion].option3,
                option4 = questionList[currentQuestion].option4,
                answer = questionList[currentQuestion].answer,
                selected = "None"
            )
            scoreCardList.add(selectedData)
        }

        isTimerRunning = false
        currentQuestion +=1
        currentTime = 60
        isTimerRunning = true

        if (currentQuestion >= questionList.size ) {
            SCORE_CARD_LIST = scoreCardList
            navController.navigate(Screens.ScorecardScreen.route+"/${questionList.size}/${score}/${correct}/${wrong}/${testType}")
        }

    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { TopBar(navController = navController, currentQuestion = (currentQuestion+1).toString(), totalQuestion = totalQuestion) },
        bottomBar = {
                    BottomAppBar(
                        containerColor = Color(0xFFEFF0F3)
                    ) {

                        Box(
                            modifier = Modifier
                                .background(color = Color(0xFFEFF0F3))
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            LaunchedEffect(key1 = currentTime, key2 = isTimerRunning) {
                                if(currentTime > 0 && isTimerRunning) {
                                    delay(1000L)
                                    currentTime -= 1
                                }
                            }
                            Button(
                                modifier = Modifier
                                    .height(55.dp)
                                    .width(250.dp)
                                    .clip(shape = RoundedCornerShape(20.dp)),
                                onClick = {
                                         if (selectedOption.isNotEmpty() && currentQuestion < questionList.size) {

                                             showProgress = false

                                             val selectedData = ScoreCardDataModel(
                                                 id = questionList[currentQuestion].id,
                                                 question = questionList[currentQuestion].question,
                                                 option1 = questionList[currentQuestion].option1,
                                                 option2 = questionList[currentQuestion].option2,
                                                 option3 = questionList[currentQuestion].option3,
                                                 option4 = questionList[currentQuestion].option4,
                                                 answer = questionList[currentQuestion].answer,
                                                 selected = selectedOption
                                             )

                                             if(selectedOption == questionList[currentQuestion].answer){
                                                 score += 2
                                                 correct += 1
                                                 scoreCardList.add(selectedData)
                                                 database
                                                     .child(selectedData.id)
                                                     .child("id")
                                                     .setValue(selectedData.id)
                                                     .addOnSuccessListener {
                                                         showProgress = false
                                                     }
                                             } else {
                                                 wrong += 1
                                                 scoreCardList.add(selectedData)
                                             }

                                             selectedOption = ""
                                             currentQuestion += 1
                                             currentTime = 60
                                             if (currentQuestion >= questionList.size ) {

                                                 SCORE_CARD_LIST = scoreCardList
                                                 navController.navigate(Screens.ScorecardScreen.route+"/${questionList.size}/${score}/${correct}/${wrong}/${testType}")

                                             }
                                         }
                                },
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedOption.isEmpty()) Color(0xFFAFBFAF) else primColor,
                                    contentColor = Color.White
                                )
                            ) {

                                Text(
                                    text = "Next",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                        }

                    }
        },
        containerColor = Color(0xFFEFF0F3),
        content = {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = it.calculateTopPadding(),
                        bottom = it.calculateBottomPadding()
                    ),
                ) {

                if (showProgress)
                    PrepGeniusProgressBar()

                Box(
                    modifier = Modifier
                        .background(color = Color(0xFFEFF0F3))
                        .fillMaxWidth()

                ) {


                                Column(
                                    modifier = Modifier
                                        .verticalScroll(state = rememberScrollState())
                                        .background(color = Color(0xFFEFF0F3))
                                        .fillMaxSize()
                                        .padding(12.dp)

                                ) {

                                    if (currentQuestion < questionList.size) {


                                        Box(
                                            modifier = Modifier
                                                .padding(top = 30.dp)
                                                .background(
                                                    color = Color.White,
                                                    shape = RoundedCornerShape(20.dp)
                                                )
                                                .fillMaxWidth()
                                                .padding(20.dp)
                                        ) {
                                            Text(
                                                modifier = Modifier
                                                    .background(color = Color.White)
                                                    .fillMaxWidth(),
                                                text = questionList[currentQuestion].question,
                                                fontFamily = Roboto,
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold,
                                                textAlign = TextAlign.Justify,
                                            )
                                        }


                                        options.forEach { item ->

                                            Spacer(modifier = Modifier.height(20.dp))

                                            Row(
                                                modifier = Modifier
                                                    .background(
                                                        color = if (selectedOption == item) Color(
                                                            0xFFABD1C6
                                                        ) else Color.White,
                                                        shape = RoundedCornerShape(15.dp)
                                                    )
                                                    .fillMaxWidth()
                                                    .selectable(
                                                        selected = selectedOption == item,
                                                        onClick = {
                                                            selectedOption = item
                                                        },
                                                        role = Role.RadioButton
                                                    )
                                                    .padding(8.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.Start
                                            ) {

                                                Text(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .fillMaxWidth(),
                                                    text = item,
                                                    fontFamily = Roboto,
                                                    fontSize = 20.sp,
                                                    textAlign = TextAlign.Left,
                                                    color = primColor
                                                )
                                                IconToggleButton(
                                                    checked = selectedOption == item,
                                                    onCheckedChange = {
                                                        selectedOption = item
                                                    }
                                                ) {

                                                    Icon(
                                                        imageVector = if (selectedOption == item) Icons.Filled.CheckCircle else Icons.Outlined.Circle,
                                                        contentDescription = "",
                                                        tint = primColor
                                                    )

                                                }

                                            }
                                        }
                                    }

                                }

                    Text(
                        modifier = Modifier
                            .background(
                                color = Color.Transparent,
                                shape = CircleShape
                            )
                            .border(
                                width = 5.dp,
                                color = Color(0xFF004643),
                                shape = CircleShape
                            )
                            .padding(15.dp)
                            .size(25.dp)
                            .wrapContentSize(align = Alignment.Center)
                            .align(Alignment.TopCenter),
                        text = currentTime.toString(),
                        fontFamily = Roboto,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )



                }
                Spacer(modifier = Modifier.height(30.dp))
            }
        }

    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    navController : NavHostController,
    totalQuestion : String = "3",
    currentQuestion : String = "1",
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    CenterAlignedTopAppBar(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        navigationIcon = {
            IconButton(
                modifier = Modifier,
                onClick = {
                    navController.popBackStack()
                }
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.back_arrow),
                    contentDescription = "Back Icon",
                    tint = primColor
                )
            }
        },
        title = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color(0xFFEFF0F3)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$currentQuestion/$totalQuestion",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFFEFF0F3),
            titleContentColor = Color.Black
        )
    )


}

@Preview(showBackground = true)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrepGeniusProgressBar() {

    AlertDialog(
        onDismissRequest = { /*TODO*/ }
    ) {
        Card (
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ){

            Row(
                modifier = Modifier
                    .padding(vertical = 14.dp, horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(60.dp),
                    color = primColor,
                    strokeWidth = 8.dp,
                    trackColor = Color(0xFFAFBFAF),
                )
                Spacer(modifier = Modifier.width(15.dp))
                Text(
                    text = "Preparing...",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = primColor
                )
            }
        }
    }
//    Box(
//        modifier = Modifier
//            .background(color = Color.Transparent)
//            .fillMaxSize(),
//        contentAlignment = Alignment.Center
//    ) {
//
//
//
//    }
}