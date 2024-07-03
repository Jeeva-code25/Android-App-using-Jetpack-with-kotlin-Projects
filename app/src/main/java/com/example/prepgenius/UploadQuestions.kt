package com.example.prepgenius

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.focus.FocusDirection
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.example.prepgenius.ui.theme.Roboto
import com.example.prepgenius.ui.theme.primColor
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.InputStream

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun UploadQuestions(
    navController : NavHostController
) {

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val database = FirebaseDatabase.getInstance().getReference("Prep Genius")

    var isUploaded by remember { mutableStateOf(false) }
    var subjectsExpanded by remember { mutableStateOf(false) }
    var subjectsSelected by rememberSaveable { mutableStateOf("") }
    var subjectsError by remember { mutableStateOf(false) }
    var subjectsFieldSize by remember{ mutableStateOf(Size.Zero) }
    val subjectsIcon = if (subjectsExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown

    var openAlertDialog by remember { mutableStateOf(false) }
    var newSubject by rememberSaveable { mutableStateOf("") }
    var newSubjectError by remember { mutableStateOf(false) }
    val newSubjects =  remember { mutableListOf<SubjectDataModel>() }
    val subjects =  remember { mutableListOf<String>() }


    var showProgress by remember { mutableStateOf(true) }
    var fileUri by remember { mutableStateOf<Uri?>(null) }
    val contentResolver = LocalContext.current.contentResolver

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        fileUri = uri
    }

    database
        .child("Subjects")
        .addValueEventListener(object : ValueEventListener
        {

            override fun onDataChange(snapshot: DataSnapshot) {
                newSubjects.clear()
                subjects.clear()
                if (snapshot.exists()) {
                    snapshot.children.forEach { item ->
                        item.getValue(SubjectDataModel::class.java)?.let {
                            newSubjects.add(it)
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


    Scaffold(
        topBar = { TopBar(navController = navController)},
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier
                    .size(50.dp),
                shape = RoundedCornerShape(8.dp),
                onClick = {
                          openAlertDialog = true
                },
                containerColor = Color(0xFFABD1C6),
                contentColor = primColor
            ) {

                Icon(imageVector = Icons.Outlined.Add, contentDescription = "logout")

            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) {

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = it.calculateTopPadding(), bottom = it.calculateBottomPadding()),
        ) {
            Column(
                modifier = Modifier
                    .background(primColor)
                    .verticalScroll(state = rememberScrollState())
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                if (showProgress)
                    PrepGeniusProgressBar()

                if (openAlertDialog) {
                    AlertDialog(modifier = Modifier.background(color = Color.Transparent),onDismissRequest = { openAlertDialog = false }) {
                        Card(
                            modifier = Modifier
                                .background(color = Color.White, shape = RoundedCornerShape(16.dp))
                                .fillMaxWidth()
                                .padding(16.dp),
                            shape = RoundedCornerShape(16.dp),
                        ) {
                            Column(
                                modifier = Modifier
                                    .background(
                                        color = Color.White,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Text(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentSize(),
                                    text = "Enter subject name  to add:",
                                    fontFamily = Roboto,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Justify,
                                    color = Color.Black
                                )

                                OutlinedTextField(
                                    value = newSubject,
                                    onValueChange = { it ->
                                        newSubjectError = false
                                        newSubject = it
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    isError = newSubjectError,
                                    keyboardOptions = KeyboardOptions(
                                        capitalization = KeyboardCapitalization.Sentences,
                                        keyboardType = KeyboardType.Text,
                                        imeAction = ImeAction.Done
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onDone = {
                                            newSubjectError = TextUtils.isEmpty(newSubject)
                                            focusManager.clearFocus()
                                        }
                                    ),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White,
                                        errorContainerColor = Color.White,
                                        unfocusedTextColor = primColor,
                                        focusedTextColor = primColor,
                                        unfocusedPlaceholderColor = primColor,
                                        focusedPlaceholderColor = primColor,
                                        focusedLabelColor = primColor,
                                        unfocusedLabelColor = primColor,
                                        errorTextColor = Color.Red,
                                        errorLabelColor = Color.Red,
                                        focusedIndicatorColor = primColor,
                                        unfocusedIndicatorColor = primColor,
                                        errorIndicatorColor = Color.Red,
                                        errorPlaceholderColor = Color.Red,
                                        cursorColor = primColor
                                    )
                                )
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    TextButton(
                                        onClick = { openAlertDialog = false },
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .weight(1f),
                                    ) {
                                        Text("Dismiss", color = Color.Gray)
                                    }
                                    TextButton(
                                        onClick = {

                                            newSubjectError = newSubject.isEmpty() || subjects.contains(newSubject.trim())

                                            if (subjects.contains(newSubject.trim())){
                                                Toast.makeText(context,"Subject already exist",Toast.LENGTH_SHORT).show()
                                            }

                                            if(!newSubjectError) {
                                                showProgress = true
                                                val id = database.push().key.toString()
                                                val newSubjectData = SubjectDataModel(
                                                    id = id,
                                                    subject = newSubject.trim()
                                                )
                                                database
                                                    .child("Subjects")
                                                    .child(id)
                                                    .setValue(newSubjectData)
                                                    .addOnSuccessListener {
                                                        showProgress = false
                                                        openAlertDialog = false
                                                        newSubject = ""
                                                        Toast.makeText(context, "Successfully new subject added",Toast.LENGTH_SHORT).show()
                                                    }
                                            }
                                        },
                                        modifier = Modifier.padding(8.dp),
                                    ) {
                                        Text("Confirm", color = primColor)
                                    }
                                }
                            }
                        }
                    }
                }

                DefaultDropDown(
                    context = context,
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
                    dropDownItems = newSubjects,
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
                            launcher.launch("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")

                        }

                    },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF8C660),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Upload",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (!isUploaded) {
                    fileUri?.let { uri ->
                        readExcelFile(
                            contentResolver,
                            uri,
                            subject = subjectsSelected,
                            context = context,
                            navController = navController
                        )
                        isUploaded = true

                    }
                }


            }
        }
    }
}

fun readExcelFile(
    contentResolver: ContentResolver,
    uri: Uri,
    subject: String,
    context: Context,
    navController: NavHostController
): String {
    return contentResolver.openInputStream(uri)?.use { inputStream ->
        parseExcel(inputStream, subject = subject,context = context, navController = navController)
    } ?: "Failed to read file"
}

fun parseExcel(
    inputStream: InputStream,
    subject: String,
    context: Context,
    navController: NavHostController
): String {

    val database = FirebaseDatabase.getInstance().getReference("Prep Genius")

    val workbook = WorkbookFactory.create(inputStream)
    val sheet = workbook.getSheetAt(0)
    val sb = StringBuilder()


    sheet.forEachIndexed { index, row ->

        if (index > 0) {
            val id = database.push().key.toString()
            var question = ""
            val options = mutableListOf<String>()
            var answer = ""

            row.forEachIndexed { inx, cell ->
                if (inx == 0) {
                    question = cell.toString()
//                    sb.append(cell.toString()).append("\n")
                } else if (inx <= 4) {
                    options.add(cell.toString())
//                    sb.append(cell.toString()).append("\t")
                } else {
                    answer = cell.toString()
//                    sb.append("Ans: $cell")
                }
            }

            val data = DataModel(
                id = id,
                question = question,
                option1 = options[0],
                option2 = options[1],
                option3 = options[2],
                option4 = options[3],
                answer = answer
            )
            database
                .child("Questions")
                .child(subject)
                .child(id).setValue(data)
                .addOnSuccessListener {

                }
//            sb.append("\n")
        }

    }

    Toast.makeText(context, "File Uploaded",Toast.LENGTH_SHORT).show()

    workbook.close()
    return sb.toString()
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
        navigationIcon = { DefaultBackArrow {
            navController.popBackStack()
        }},
        title = {
            Text(
                text = "Upload your Questions",
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
    context: Context,
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
    dropDownItems: List<SubjectDataModel>,
    dropDownOnclick: (String)->Unit,
) {

    val database = FirebaseDatabase.getInstance().getReference("Prep Genius")

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
                    text = { Text(text = item.subject) },
                    trailingIcon = { IconButton(onClick = {
                        database
                            .child("Subjects")
                            .child(item.id)
                            .removeValue()
                            .addOnSuccessListener {
                                Toast.makeText(context,"Successfully Removed",Toast.LENGTH_SHORT).show()
                                onDismiss()
                            }
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "delete",
                            tint = Color.Red
                        )
                    }},
                    onClick = {
                        dropDownOnclick(item.subject)
                    })
            }

        }
    }

}