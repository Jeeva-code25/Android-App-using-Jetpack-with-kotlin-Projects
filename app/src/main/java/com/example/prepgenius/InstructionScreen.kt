package com.example.prepgenius

import android.content.Context
import android.text.TextUtils
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

var TOTAL_TEST_QUESTIONS : List<DataModel> = emptyList()

@Composable
fun InstructionScreen(
    navController : NavHostController,
    testType : String = ""
) {


    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val sharedPreference = context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE)

    val database = FirebaseDatabase.getInstance().getReference("Prep Genius")

    val attemptedQuestions =  remember { mutableListOf<QuestionDataModel>() }
    val botanyQuestions =  remember { mutableListOf<DataModel>() }
    val zoologyQuestions =  remember { mutableListOf<DataModel>() }
    val physicsQuestions =  remember { mutableListOf<DataModel>() }
    val chemistryQuestions =  remember { mutableListOf<DataModel>() }
    val mathsQuestions =  remember { mutableListOf<DataModel>() }
    val othersQuestions =  remember { mutableListOf<DataModel>() }
    var totalQuestions =  remember { mutableListOf<DataModel>() }


    var testQuestions by remember { mutableStateOf("") }
    var isTestQuestionsError by remember { mutableStateOf(false) }

    var attemptedQuestionProgress by remember { mutableStateOf(true) }
    var botanyProgress by remember { mutableStateOf(true) }
    var zoologyProgress by remember { mutableStateOf(true) }
    var physicsProgress by remember { mutableStateOf(true) }
    var chemistryProgress by remember { mutableStateOf(true) }
    var mathsProgress by remember { mutableStateOf(true) }
    var showProgress by remember { mutableStateOf(true) }



    database
        .child("Users")
        .child(sharedPreference.getString("PHONE","").toString())
        .child("Attempted Questions")
        .addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                attemptedQuestions.clear()
                snapshot.children.forEach {item->

                    item.getValue(QuestionDataModel::class.java)?.let {

                        attemptedQuestions.add(it)
                    }
                }
                attemptedQuestionProgress = false
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }

        })


    if (!attemptedQuestionProgress) {
        database
            .child("Questions")
            .child("Botany")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    botanyQuestions.clear()
                    snapshot.children.forEach { item ->

                        item.getValue(DataModel::class.java)?.let {

                            if(!attemptedQuestions.contains(QuestionDataModel(id= it.id)))
                                botanyQuestions.add(it)
                        }
                    }
                    botanyProgress = false
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                }

            }
            )

        database
            .child("Questions")
            .child("Zoology")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    zoologyQuestions.clear()
                    snapshot.children.forEach { item ->

                        item.getValue(DataModel::class.java)?.let {
                            if(!attemptedQuestions.contains(QuestionDataModel(id= it.id)))
                                zoologyQuestions.add(it)
                        }
                    }
                    zoologyProgress = false

                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                }

            }
            )

        database
            .child("Questions")
            .child("Physics")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    physicsQuestions.clear()
                    snapshot.children.forEach { item ->

                        item.getValue(DataModel::class.java)?.let {
                            if(!attemptedQuestions.contains(QuestionDataModel(id= it.id)))
                                physicsQuestions.add(it)
                        }
                    }
                    physicsProgress = false
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                }

            }
            )

        database
            .child("Questions")
            .child("Chemistry")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    chemistryQuestions.clear()
                    snapshot.children.forEach { item ->

                        item.getValue(DataModel::class.java)?.let {
                            if(!attemptedQuestions.contains(QuestionDataModel(id= it.id)))
                                chemistryQuestions.add(it)
                        }
                    }
                    chemistryProgress = false

                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                }

            }
            )

        database
            .child("Questions")
            .child("Maths")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    mathsQuestions.clear()
                    snapshot.children.forEach { item ->

                        item.getValue(DataModel::class.java)?.let {
                            if(!attemptedQuestions.contains(QuestionDataModel(id= it.id)))
                                mathsQuestions.add(it)
                        }

                    }
                    mathsProgress = false

                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                }

            }
            )
    }


    database
        .child("Questions")
        .child(testType)
        .addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                othersQuestions.clear()
                if (snapshot.exists()) {
                    snapshot.children.forEach { item ->

                        item.getValue(DataModel::class.java)?.let {
                            if (!attemptedQuestions.contains(QuestionDataModel(id = it.id)))
                                othersQuestions.add(it)
                        }

                    }
                }
                showProgress = false

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }

        }
        )



    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { TopBar(navController = navController) },
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
                        .background(color = primColor)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(15.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    if (botanyProgress || zoologyProgress || physicsProgress || chemistryProgress || mathsProgress || attemptedQuestionProgress || showProgress)
                        PrepGeniusProgressBar()

                    Image(
                        modifier = Modifier
                            .size(250.dp),
                        painter = painterResource(id = R.drawable.instruct_screen_removebg),
                        contentDescription = "Logo",
                        contentScale = ContentScale.FillBounds
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Note:",
                        fontFamily = Roboto,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic,
                        color = Color.LightGray,
                        textAlign = TextAlign.Justify
                    )
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize(),
                        text = "It is a Practice test of NEET/JEE." +
                                "Questions comes from Maths, Physics, Chemistry and Biology." +
                                "Each correct answer you will get 2 marks." +
                                "In Before start of the test Enter how much question you will practice." +
                                "After finished the test scorecard will be displayed."
                        ,
                        fontFamily = Roboto,
                        fontSize = 17.sp,
                        fontStyle = FontStyle.Italic,
                        color = Color.LightGray,
                        textAlign = TextAlign.Justify
                    )

                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize(),
                        text = "Enter how much question you want to practice:",
                        fontFamily = Roboto,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Justify,
                        color = Color.LightGray
                    )

                    OutlinedTextField(
                        value = testQuestions,
                        onValueChange = { it ->
                            isTestQuestionsError = false
                            testQuestions = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = isTestQuestionsError,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                isTestQuestionsError = TextUtils.isEmpty(testQuestions)
                                focusManager.clearFocus()
                            }
                        ),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = primColor,
                            unfocusedContainerColor = primColor,
                            errorContainerColor = primColor,
                            unfocusedTextColor = Color.LightGray,
                            focusedTextColor = Color.LightGray,
                            errorTextColor = Color.Red,
                            errorLabelColor = Color.Red,
                            focusedIndicatorColor = Color.LightGray,
                            unfocusedIndicatorColor = Color.LightGray,
                            errorIndicatorColor = Color.Red,
                            errorPlaceholderColor = Color.Red,
                            cursorColor = Color.LightGray
                        )
                    )

                    Spacer(modifier = Modifier.height(15.dp))
                    Button(
                        modifier = Modifier
                            .height(55.dp)
                            .width(150.dp)
                            .clip(shape = RoundedCornerShape(20.dp)),
                        onClick = {

                            totalQuestions.clear()
                            when (testType) {
                                "NEET" -> {
                                    totalQuestions .addAll (botanyQuestions + zoologyQuestions + physicsQuestions + chemistryQuestions)
                                    totalQuestions = totalQuestions.toMutableList().apply { shuffle() }
                                }

                                "JEE" -> {
                                    totalQuestions .addAll(mathsQuestions + physicsQuestions + chemistryQuestions)
                                    totalQuestions = totalQuestions.toMutableList().apply { shuffle() }
                                }

                                "Botany" -> {
                                    totalQuestions = botanyQuestions.toMutableList().apply { shuffle() }
                                }

                                "Zoology" -> {
                                    totalQuestions = zoologyQuestions.toMutableList().apply { shuffle() }
                                }

                                "Maths" -> {
                                    totalQuestions = mathsQuestions.toMutableList().apply { shuffle() }
                                }

                                "Physics" -> {
                                    totalQuestions = physicsQuestions.toMutableList().apply { shuffle() }
                                }

                                "Chemistry" -> {
                                    totalQuestions = chemistryQuestions.toMutableList().apply { shuffle() }
                                }
                                else -> {
                                    totalQuestions = othersQuestions.toMutableList().apply { shuffle() }
                                }


                            }

                            isTestQuestionsError = TextUtils.isEmpty(testQuestions)

                            if (!isTestQuestionsError){
                                if (testQuestions.toIntOrNull()!! <= totalQuestions.size) {
                                    TOTAL_TEST_QUESTIONS =
                                        totalQuestions.take(testQuestions.toInt())
                                    navController.navigate(Screens.TestScreen.route+"/${testType}/${testQuestions}")
                                } else {
                                    isTestQuestionsError = true
                                    Toast.makeText(context,"You haven't enough question to start ${totalQuestions.size} only available",Toast.LENGTH_SHORT).show()

                                }
                            }

                        },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF8C660),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Start",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    navController : NavHostController
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    TopAppBar(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        navigationIcon = {
            DefaultBackArrow { navController.popBackStack()}
        },
        title = {},
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = primColor,
            titleContentColor = Color.White
        )
    )

}

@Composable
fun DefaultBackArrow(
    modifier : Modifier = Modifier,
    icon : ImageVector =  ImageVector.vectorResource(id = R.drawable.back_arrow),
    onclick: () -> Unit
) {
    IconButton(
        modifier = modifier,
        onClick = {
            onclick()
        }
    ) {
        Icon(
            imageVector =icon,
            contentDescription = "Back Icon",
            tint = Color.White
        )
    }


}

