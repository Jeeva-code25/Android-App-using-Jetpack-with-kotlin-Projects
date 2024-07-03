package com.example.prepgenius

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.navigation.NavHostController
import com.example.prepgenius.ui.theme.Roboto
import com.example.prepgenius.ui.theme.primColor
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@Composable
fun SpecificSubjectsScreen(
    navController: NavHostController
) {

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val database = FirebaseDatabase.getInstance().getReference("Prep Genius")

    var subjectsExpanded by remember { mutableStateOf(false) }
    var subjectsSelected by rememberSaveable { mutableStateOf("") }
    var subjectsError by remember { mutableStateOf(false) }
    var subjectsFieldSize by remember{ mutableStateOf(Size.Zero) }
    val subjectsIcon = if (subjectsExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown
    val subjects =  remember { mutableListOf<String>() }
    var showProgress by remember { mutableStateOf(true) }


    database
        .child("Subjects")
        .addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot) {
                subjects.clear()
                if (snapshot.exists()) {
                    snapshot.children.forEach { item ->
                        item.getValue(SubjectDataModel::class.java)?.let {
                            subjects.add(it.subject)
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

    Scaffold (
        modifier = Modifier.fillMaxSize(),
        topBar = { TopBar(navController) },
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

                Box (
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = primColor),
                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        modifier = Modifier
                            .verticalScroll(state = rememberScrollState())
                            .background(color = primColor)
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(25.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        if (showProgress)
                            PrepGeniusProgressBar()
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentSize(),
                            text = "Choose a Subject: ",
                            fontFamily = Roboto,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Justify,
                            color = Color.LightGray
                        )
                        DefaultDropDown(
                            modifier = Modifier
                                .height(50.dp)
                                .fillMaxWidth()
                                .background(primColor)
                                .onGloballyPositioned { layoutCoordinates ->
                                    subjectsFieldSize = layoutCoordinates.size.toSize()
                                },
                            selectedText = subjectsSelected,
                            onValueChange = {
                                subjectsSelected = it
                            },
                            placeholderText = "Select a Subject",
                            expanded = subjectsExpanded,
                            trailingIcon = {
                                Icon(
                                    imageVector = subjectsIcon,
                                    contentDescription = "DropdownIcon",
                                    modifier = Modifier
                                        .clickable {
                                            subjectsExpanded = !subjectsExpanded
                                        },
                                    tint = Color.LightGray
                                )
                            },
                            textError = subjectsError,
                            onDismiss = {
                                subjectsExpanded = false
                            },
                            dropDownItems = subjects,
                            dropDownModifier = Modifier
                                .width(with(LocalDensity.current) { subjectsFieldSize.width.toDp() }),
                            dropDownOnclick = {
                                subjectsError = false
                                subjectsSelected = it
                                subjectsExpanded = false
                                focusManager.clearFocus()
                            }
                        )
                        Button(
                            modifier = Modifier
                                .height(55.dp)
                                .width(160.dp)
                                .clip(shape = RoundedCornerShape(20.dp)),
                            onClick = {

                                subjectsError = subjectsSelected.isEmpty()
                                if (!subjectsError){
                                    navController.navigate(Screens.InstructionScreen.route+"/${subjectsSelected}")
                                }

                            },
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFF8C660),
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = "Done",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

//                        Button(
//                            modifier = Modifier
//                                .height(55.dp)
//                                .width(280.dp)
//                                .clip(shape = RoundedCornerShape(20.dp)),
//                            onClick = {
//                                navController.navigate(Screens.InstructionScreen.route + "/${"Physics"}")
//                            },
//                            shape = RoundedCornerShape(20.dp),
//                            colors = ButtonDefaults.buttonColors(
//                                containerColor = Color.Blue,
//                                contentColor = Color.White
//                            )
//                        ) {
//                            Text(
//                                text = "Physics",
//                                color = Color.White,
//                                fontSize = 18.sp,
//                                fontWeight = FontWeight.Bold
//                            )
//                        }
//
//                        Button(
//                            modifier = Modifier
//                                .height(55.dp)
//                                .width(280.dp)
//                                .clip(shape = RoundedCornerShape(20.dp)),
//                            onClick = {
//                                navController.navigate(Screens.InstructionScreen.route + "/${"Chemistry"}")
//
//                            },
//                            shape = RoundedCornerShape(20.dp),
//                            colors = ButtonDefaults.buttonColors(
//                                containerColor = Color(0xFFB2BEB5),
//                                contentColor = Color.White
//                            )
//                        ) {
//                            Text(
//                                text = "Chemistry",
//                                color = Color.White,
//                                fontSize = 18.sp,
//                                fontWeight = FontWeight.Bold
//                            )
//                        }
//
//                        Button(
//                            modifier = Modifier
//                                .height(55.dp)
//                                .width(280.dp)
//                                .clip(shape = RoundedCornerShape(20.dp)),
//                            onClick = {
//                                navController.navigate(Screens.InstructionScreen.route + "/${"MATHS"}")
//                            },
//                            shape = RoundedCornerShape(20.dp),
//                            colors = ButtonDefaults.buttonColors(
//                                containerColor = Color.Red,
//                                contentColor = Color.White
//                            )
//                        ) {
//                            Text(
//                                text = "Maths",
//                                color = Color.White,
//                                fontSize = 18.sp,
//                                fontWeight = FontWeight.Bold
//                            )
//                        }
//
//                        Button(
//                            modifier = Modifier
//                                .height(55.dp)
//                                .width(280.dp)
//                                .clip(shape = RoundedCornerShape(20.dp)),
//                            onClick = {
//                                navController.navigate(Screens.InstructionScreen.route + "/${"BIOLOGY"}")
//                            },
//                            shape = RoundedCornerShape(20.dp),
//                            colors = ButtonDefaults.buttonColors(
//                                containerColor = Color(0xFF53B175),
//                                contentColor = Color.White
//                            )
//                        ) {
//                            Text(
//                                text = "Biology",
//                                color = Color.White,
//                                fontSize = 18.sp,
//                                fontWeight = FontWeight.Bold
//                            )
//                        }

                    }
                }
            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
   navController: NavHostController
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    TopAppBar(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        navigationIcon = {
            DefaultBackArrow { navController.popBackStack()}
        },
        title = {
            Text(
                text = "Train your Area",
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

@Composable
private fun DefaultDropDown(
    modifier : Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(12.dp),
    dropDownModifier : Modifier,
    selectedText : String,
    onValueChange: (String) -> Unit,
    placeholderText:String = "",
    expanded : Boolean,
    trailingIcon: @Composable () -> Unit,
    textError: Boolean,
    onDismiss : ()->Unit,
    dropDownItems: List<String>,
    dropDownOnclick: (String)->Unit,
) {

    Column(
        modifier = Modifier
            .padding(top =10.dp)
    ) {

        OutlinedTextField(
            value = selectedText,
            onValueChange = {onValueChange(it)},
            modifier = modifier,
            shape = shape,
            textStyle = TextStyle().copy(fontFamily = Roboto, fontSize = 13.sp, letterSpacing = 1.sp),
            placeholder = {
                Text(
                    text = placeholderText ,
                    fontFamily = Roboto,
                    fontSize = 13.sp,
                    color = Color.LightGray
                )
            },
            trailingIcon = { trailingIcon() },
            isError = textError,
            readOnly = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = primColor,
                errorContainerColor = primColor,
                unfocusedContainerColor = primColor,
                unfocusedTextColor = Color.LightGray,
                focusedTextColor = Color.LightGray,
                errorTextColor = Color.Red,
                errorLabelColor = Color.Red,
                focusedIndicatorColor = Color.LightGray,
                unfocusedIndicatorColor = Color.LightGray,
                errorIndicatorColor = Color.Red,
                cursorColor = Color.LightGray,
                unfocusedPlaceholderColor = Color.LightGray,
                errorPlaceholderColor = Color.Red
            )

        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onDismiss() },
            modifier =dropDownModifier,
        ) {

            dropDownItems.forEach {item ->
                DropdownMenuItem(
                    text = { Text(text = item) },
                    onClick = {
                        dropDownOnclick(item)
                    })
            }

        }
    }

}