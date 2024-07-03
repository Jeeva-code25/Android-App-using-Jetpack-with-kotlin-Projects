package com.example.prepgenius

import android.content.Context
import android.text.TextUtils
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.prepgenius.ui.theme.primColor
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@Composable
fun LogInScreen(
    navController : NavHostController
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val sharedPreference = context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE)
    val sharedEdit = sharedPreference.edit()

    val database = FirebaseDatabase.getInstance().getReference("Prep Genius").child("Users")

    var phone by remember { mutableStateOf("") }
    var isPhoneError by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var isPasswordError by remember { mutableStateOf(false) }
    var showPassword by remember { mutableStateOf(false) }
    var showProgress by remember { mutableStateOf(false) }


    val newRegisterText =
        buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    fontSize =12.sp,
                    color = Color.White
                )
            ){
                append("Don't have an account? ")
            }
            withStyle(
                style = SpanStyle(
                    fontSize =12.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )
            ) {
                append("Register")
            }
        }

    Column (
        modifier = Modifier
            .verticalScroll(state = rememberScrollState())
            .fillMaxSize()
            .background(color = primColor)
            .padding(25.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        if (showProgress)
            PrepGeniusProgressBar()

        Image(
            modifier = Modifier
                .height(250.dp)
                .width(300.dp),
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                contentScale = ContentScale.FillBounds
            )

            Column() {

                Text(
                    text = "Welcome Back",
                    modifier = Modifier,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                    color = Color.White
                )

                Text(
                    text = "Login to your account",
                    modifier = Modifier,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { it ->
                        isPhoneError = false
                        if (it.length <= 10)
                            phone = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Enter your phone", color = Color.LightGray) },
                    placeholder = { Text(text = "Mobile Number", color = Color.LightGray) },
                    singleLine = true,
                    isError = isPhoneError,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            isPhoneError = TextUtils.isEmpty(phone) || phone.length < 10
                            focusManager.clearFocus()

                        }
                    ),
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
                        errorPlaceholderColor = Color.Red
                    )
                )
                if (isPhoneError)
                    Text(text = "Field is Mandatory", color = Color.Red, fontSize = 12.sp)

                Spacer(modifier = Modifier.height(15.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { it ->
                        isPasswordError = false
                        password = it.filter { it.isDigit() }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Enter your password", color = Color.LightGray) },
                    placeholder = { Text(text = "Password", color = Color.LightGray) },
                    singleLine = true,
                    isError = isPasswordError,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.NumberPassword,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            isPhoneError = TextUtils.isEmpty(phone)
                            isPasswordError =
                                TextUtils.isEmpty(password) || (password.length < 8 || password.length > 16)

                            focusManager.clearFocus()
                        }
                    ),
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                contentDescription = if (showPassword) "showPassword" else "hidePassword",
                                tint = Color.LightGray
                            )
                        }
                    },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),

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
                        errorPlaceholderColor = Color.Red
                    )
                )
                if (isPasswordError && password.isNotEmpty())
                    Text(text = "Password Mismatch", color = Color.Red, fontSize = 12.sp)

                Spacer(modifier = Modifier.height(30.dp))
                Button(
                    modifier = Modifier
                        .height(55.dp)
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(20.dp)),
                    onClick = {
                        isPhoneError = TextUtils.isEmpty(phone)
                        isPasswordError =
                            TextUtils.isEmpty(password) || (password.length < 8 || password.length > 16)

                        if (!isPhoneError && !isPasswordError) {

                            showProgress = true

                            database
                                .child("+91$phone")
                                .child("Profile")
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {

                                        if (snapshot.exists()) {

                                            val profile =
                                                snapshot.getValue(ProfileData::class.java)!!
                                            showProgress = false

                                            if (profile.password == password) {
                                                sharedEdit.putBoolean("USER_SIGNED", true)
                                                sharedEdit.putString("PHONE", profile.phone)
                                                sharedEdit.putString("USERNAME", profile.username)
                                                sharedEdit.apply()

                                                Toast.makeText(
                                                    context,
                                                    "Login Successful",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                navController.navigate(MIDDLE)

                                            } else {
                                                isPasswordError = true
                                            }

                                        } else {
                                            showProgress = false
                                            Toast.makeText(
                                                context,
                                                "Account not found",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        Toast.makeText(context, error.message, Toast.LENGTH_SHORT)
                                            .show()
                                    }

                                }
                                )

                        }

                    },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF8C660),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "LogIn",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                TextButton(onClick = {
                    navController.navigate(Screens.RegisterScreen.route)
                })
                {
                    Text(
                        text = newRegisterText,
                    )
                }
            }

    }

}