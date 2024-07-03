package com.example.prepgenius

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import com.example.prepgenius.ui.theme.Roboto
import com.example.prepgenius.ui.theme.primColor
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.itextpdf.text.BaseColor
import com.itextpdf.text.Document
import com.itextpdf.text.Font
import com.itextpdf.text.Paragraph
import com.itextpdf.text.Phrase
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.text.Element
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScorecardScreen(
    navController : NavHostController,
    total : String = "4",
    score : String = "6000",
    correctAns : String = "3",
    wrongAns : String = "0",
    testType: String
) {

    val context = LocalContext.current

    val questionList = SCORE_CARD_LIST
    var pressedTime = 0L

    val sharedPreference = context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE)

    val permissionState = rememberPermissionState(permission = android.Manifest.permission.READ_MEDIA_IMAGES)



    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            val file = generatePDF(context,"Edu Guide Test_Report",questionList,testType)

            if (file != null) {
                sendPdfViaSms(context, file)
            } else {
                Toast.makeText(context, "Failed to generate PDF", Toast.LENGTH_SHORT).show()
            }

        } else {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()

        }
    }


    val crctAnsText =
        buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    fontSize =15.sp,
                    fontWeight = FontWeight.Bold,
                    )
            ){
                append("Correct Answer: ")
            }
            withStyle(
                style = SpanStyle(
                    fontSize =15.sp,
                )
            ) {
                append(correctAns)
            }
        }
    val wrongAnsText =
        buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    fontSize =15.sp,
                    fontWeight = FontWeight.Bold,
                )
            ){
                append("Wrong Answer:   ")
            }
            withStyle(
                style = SpanStyle(
                    fontSize =15.sp,
                )
            ) {
                append(wrongAns)
            }
        }
    BackHandler(
        enabled = true
    ) {

        if (pressedTime+2000 > System.currentTimeMillis()){
            navController.popBackStack(
                route = Screens.HomeScreen.route,
                inclusive = false
            )
        } else {
            Toast.makeText(context, "Press again  Back to Home",Toast.LENGTH_SHORT).show()
        }

        pressedTime = System.currentTimeMillis()

    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { TopBar(navController = navController) },
        containerColor = Color(0xFFEFF0F3),
        content = {

            Surface(
                modifier = Modifier
                    .background(color = Color(0xFFEFF0F3))
                    .fillMaxSize()
                    .padding(
                        top = it.calculateTopPadding(),
                        bottom = it.calculateBottomPadding()
                    ),

                ) {

                LazyColumn(
                    modifier = Modifier
                        .background(color = Color(0xFFEFF0F3))
                        .fillMaxSize()

                ) {

                    item {
                        Column(
                            modifier = Modifier
                                .padding(top = 12.dp, start = 12.dp, end = 12.dp)
                                .background(color = Color.White, shape = RoundedCornerShape(20.dp))
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp),
                        ) {
                            Text(
                                modifier = Modifier
                                    .background(color = Color.White, shape = CircleShape)
                                    .border(
                                        width = 5.dp,
                                        color = Color(0xFF004643),
                                        shape = CircleShape
                                    )
                                    .padding(15.dp)
                                    .wrapContentSize(align = Alignment.Center)
                                    .align(Alignment.CenterHorizontally),
                                text = score,
                                fontFamily = Roboto,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                modifier = Modifier
                                    .background(color = Color.White)
                                    .fillMaxWidth(),
                                text = crctAnsText,
                            )
                            Text(
                                modifier = Modifier
                                    .background(color = Color.White)
                                    .fillMaxWidth(),
                                text = wrongAnsText,
                            )

                            Button(
                                modifier = Modifier
                                    .height(45.dp)
                                    .clip(shape = RoundedCornerShape(16.dp))
                                    .align(alignment = Alignment.CenterHorizontally),
                                onClick = {
                                          navController.popBackStack(route = Screens.HomeScreen.route,inclusive = false)
                                },
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFF8C660),
                                    contentColor = Color.White
                                )
                            ) {
                                Text(
                                    text = "Back to Home",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Button(
                                modifier = Modifier
                                    .height(45.dp)
                                    .clip(shape = RoundedCornerShape(16.dp))
                                    .align(alignment = Alignment.CenterHorizontally),
                                onClick = {

                                    if (questionList.isNotEmpty()) {

                                        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.S) {
                                            when (permissionState.status) {
                                                PermissionStatus.Granted -> {
                                                    val file = generatePDF(
                                                        context,
                                                        "Edu Guide Test_Report",
                                                        questionList,
                                                        testType
                                                    )

                                                    if (file != null) {
                                                        sendPdfViaSms(context, file)
                                                    } else {
                                                        Toast.makeText(
                                                            context,
                                                            "Failed to share report",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                }

                                                is PermissionStatus.Denied -> {
                                                    permissionState.launchPermissionRequest()
                                                }
                                            }
                                        } else {

                                        when (PackageManager.PERMISSION_GRANTED) {
                                            ContextCompat.checkSelfPermission(
                                                context,
                                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                                            ) -> {
                                                val file = generatePDF(context,"Edu Guide Test_Report",questionList,testType)

                                                if (file != null) {
                                                    sendPdfViaSms(context, file)
                                                } else {
                                                    Toast.makeText(context, "Failed to share report", Toast.LENGTH_SHORT).show()
                                                }
                                            }

                                            else -> launcher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

                                        }
                                        }
                                    } else {
                                        Toast.makeText(context, "Unable to generate  report", Toast.LENGTH_LONG).show()
                                    }
                                },
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF4361EE),
                                    contentColor = Color.White
                                )
                            ) {
                                Text(
                                    text = "Share as Report",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                    }

                        items(count = questionList.size){
                            Column(
                                modifier = Modifier
                                    .padding(horizontal = 12.dp)
                            ) {
                                ResponseItem(
                                    question =questionList[it].question,
                                    selected = questionList[it].selected,
                                    correctAns = questionList[it].answer
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }

                }
            }


        }
    )

}

fun sendPdfViaSms(context: Context, file: File) {
    val uri: Uri = FileProvider.getUriForFile(
        context, "${context.packageName}.fileprovider", file
    )

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra("address", "+918838357996")  // Replace with the recipient's number
        putExtra("sms_body", "Here is the PDF file.")
    }

    // Grant temporary read permission to the SMS app
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

    // Verify that the intent will resolve to an activity
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    } else {
        Toast.makeText(context, "No app can handle this request.", Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun ResponseItem(
    question : String = "question 1",
    selected : String = "option a",
    correctAns: String = "option a"
) {

    val textColor = when(selected){
        correctAns -> primColor
        "None" -> Color.Black
        else -> {Color(0xFFDD4643)}
    }

    val bgColor = when(selected){
        correctAns -> Color(0xFFABD1C6)
        "None" -> Color.White
        else -> {Color(0xFFDD8F85)}
    }

    val correctText =
        buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    fontSize =14.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            ){
                append("Ans: ")
            }
            withStyle(
                style = SpanStyle(
                    fontSize =14.sp,
                    color = textColor
                )
            ) {
                append(correctAns)
            }
        }
    val selectedText =
        buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    fontSize =14.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            ){
                append("Selected: ")
            }
            withStyle(
                style = SpanStyle(
                    fontSize =14.sp,
                    color = textColor
                )
            ) {
                append(selected)
            }
        }
    var isExpanded by remember { mutableStateOf(false) }
    val rotateState by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f, label = "")


    Column(
        modifier = Modifier
            .background(color = bgColor, shape = RoundedCornerShape(20.dp))
            .fillMaxWidth()
            .padding(8.dp)
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearOutSlowInEasing
                )
            ),
    ) {
        Row(
            modifier = Modifier
                .background(color = bgColor, shape = RoundedCornerShape(20.dp))
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .weight(1f),
                text = question,
                fontSize = 14.sp,
                textAlign = TextAlign.Justify,
                maxLines = if (!isExpanded) 2 else Int.MAX_VALUE,
                overflow = TextOverflow.Ellipsis,
                color = textColor

            )
            IconButton(
                modifier = Modifier.rotate(rotateState),
                onClick = { isExpanded = !isExpanded }
            ) {
                Icon(
                    imageVector = Icons.Outlined.KeyboardArrowDown,
                    contentDescription = "Arrow",
                    tint = textColor

                )
            }
        }

        if (isExpanded) {
            Row(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .background(color = bgColor, shape = RoundedCornerShape(20.dp))
                    .fillMaxWidth()

            ) {

                Text(
                    modifier = Modifier
                        .weight(1f),
                    text = correctText
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    modifier = Modifier
                        .weight(1f),
                    text = selectedText
                )
            }
        }
    }
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
        title = {
            Text(
                text = "Report Card",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFFEFF0F3),
            titleContentColor = primColor
        )
    )

}


private fun generatePDF(
    context: Context,
    fileName: String,
    attendedQuestions: List<ScoreCardDataModel>,
    testType: String
) :File?{


    try {


        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val now = Date()
        val fileNameTimeStamp = dateFormat.format(now)


        val filePath =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .toString() + "/" + fileName + "_" + testType + "_" + fileNameTimeStamp + ".pdf"

        val document = Document()
        val file = File(filePath)
        PdfWriter.getInstance(document, FileOutputStream(file))
        document.open()

        val headerFont =
            Font(Font.FontFamily.TIMES_ROMAN, 18f, Font.BOLD, BaseColor(Color.White.toArgb()))
        val titleFont = Font(Font.FontFamily.TIMES_ROMAN, 20f, Font.BOLD)

        // Create a table for image, title, and subtitle
        val table = PdfPTable(2)
        table.widthPercentage = 100f
        table.spacingBefore = 10f
        table.spacingAfter = 10f

//     Add image to the table
        val drawable = ContextCompat.getDrawable(context, R.drawable.log)
        val bitmap = (drawable as BitmapDrawable).bitmap
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val image = com.itextpdf.text.Image.getInstance(stream.toByteArray())
        image.scaleToFit(150f, 150f)
        val imageCell = PdfPCell(image, true)
        imageCell.verticalAlignment = Element.ALIGN_TOP
        imageCell.border = PdfPCell.NO_BORDER
        table.addCell(imageCell)

//     Add title and subtitle to the table
        val titleText = "$testType Test Report"
        val subtitleText = "EDU GUIDE"
        val titleSubtitleCell = PdfPCell()
        titleSubtitleCell.addElement(Paragraph(titleText, titleFont))
        titleSubtitleCell.addElement(Paragraph(subtitleText, titleFont))
        titleSubtitleCell.verticalAlignment = Element.ALIGN_TOP
        titleSubtitleCell.horizontalAlignment = 20
        titleSubtitleCell.border = PdfPCell.NO_BORDER
        table.addCell(titleSubtitleCell)

        document.add(table)

//     Add table headers
        val tableHeaders = arrayOf("S.no", "Questions", "Selected", "Correct Answer")
        val headersTable = PdfPTable(tableHeaders.size)
        headersTable.widthPercentage = 100f
        headersTable.spacingBefore = 10f
        headersTable.spacingAfter = 10f
        tableHeaders.forEach { headerText ->
            val cell = PdfPCell(Phrase(headerText, headerFont))
            cell.backgroundColor = BaseColor(primColor.toArgb())
            cell.setPadding(12f)
            cell.verticalAlignment = PdfPCell.ALIGN_CENTER
            cell.horizontalAlignment = PdfPCell.ALIGN_CENTER
            headersTable.addCell(cell)
        }



        attendedQuestions.forEachIndexed { index, question ->

            val textColor = when (question.selected) {
                question.answer -> primColor
                "None" -> Color.Black
                else -> {
                    Color(0xFFDD4643)
                }
            }

            val bgColor = when (question.selected) {
                question.answer -> Color(0xFFABD1C6)
                "None" -> Color.White
                else -> {
                    Color(0xFFDD8F85)
                }
            }

            val cellFont = Font(
                Font.FontFamily.TIMES_ROMAN, 18f, Font.NORMAL,
                BaseColor(textColor.toArgb())
            )

            for (i in tableHeaders.indices) {

                val cell = when (i) {
                    0 -> PdfPCell(Phrase((index + 1).toString(), cellFont))
                    1 -> PdfPCell(Phrase(question.question, cellFont))
                    2 -> PdfPCell(Phrase(question.selected, cellFont))
                    3 -> PdfPCell(Phrase(question.answer, cellFont))
                    else -> PdfPCell(Phrase("No data", cellFont))
                }

                cell.setPadding(12f)
                cell.horizontalAlignment = PdfPCell.ALIGN_CENTER
                cell.verticalAlignment = PdfPCell.ALIGN_CENTER

                cell.backgroundColor = BaseColor(bgColor.toArgb())

                headersTable.addCell(cell)
            }

        }


        document.add(headersTable)

        document.close()

        Toast.makeText(context, "Successfully Report Generated", Toast.LENGTH_SHORT).show()
        return file
    }
    catch (e : Exception){
        e.printStackTrace()
        return null
    }

}