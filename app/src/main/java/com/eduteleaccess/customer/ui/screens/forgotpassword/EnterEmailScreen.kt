package com.eduteleaccess.customer.ui.screens.forgotpassword

import com.eduteleaccess.customer.ui.screens.loginscreen.LoginViewModel
import com.eduteleaccess.customer.ui.screens.loginscreen.LoginViewModelFactory

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.eduteleaccess.customer.R
import com.eduteleaccess.customer.ui.util.DismissFailureDialog

@Composable
fun EnterEmailScreen(
    navController: NavController
) {

    val context = LocalContext.current
    val viewModel: ForgotViewModel = viewModel(
        factory = ForgotViewModelFactory(context)
    )

    val state by viewModel.uiState.collectAsState()
    val codeDialog by viewModel.codeDialog.collectAsState()
    val passwordDialog by viewModel.passwordDialog.collectAsState()
    val resetSuccessful by viewModel.resetSuccessful.collectAsState()
    val requestError by viewModel.requestError.collectAsState()

    var email by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var password by remember {
        mutableStateOf("")
    }
    var passwordConfirm by remember {
        mutableStateOf("")
    }

    var passwordVisible by remember {
        mutableStateOf(false)
    }

    var passwordConfirmVisible by remember {
        mutableStateOf(false)
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F8FB))
            .imePadding()

    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .widthIn(max = 420.dp),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(1.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {

                Column(
                    modifier = Modifier
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    // LOGO
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = null,
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                    )

                    // BRAND TEXT
                    Text(
                        buildAnnotatedString {
                            withStyle(SpanStyle(color = Color(0xFF0156A6))) {
                                append("EDUTELE ")
                            }
                            withStyle(SpanStyle(color = Color(0xFFCE8D00))) {
                                append("ACCESS")
                            }
                        },
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Text(
                        text = "Secure access to your account",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // EMAIL
                    OutlinedTextField(
                        value = email,
                        onValueChange = {email = it},
                        label = { Text(
                            "Email or Code",
                            color = Color.Gray
                        ) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    // LOGIN BUTTON
                    Button(
                        onClick = {viewModel.requestResetCode(email)},
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(8.dp),
                        enabled = !state.isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0156A6),
                            contentColor = Color.White
                        )
                    ) {

                        if (state.isLoading) {

                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )

                        } else {

                            Text(
                                text = "Request Code",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                            )
                        }
                    }
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        TextButton(
                            onClick = {
                                // navigate to login Screen
                                navController.navigate("login")
                            }
                        ) {
                            Text(
                                "Cancel",
                                color = Color(0xFF0156A6),
                                fontSize = 13.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    // ERROR
                    state.error?.let {

                        Text(
                            text = it,
                            color = Color(0xFFD32F2F),
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        if(codeDialog){

            AlertDialog(

                onDismissRequest = {
                    viewModel.closeCodeDialog()
                },
                shape = RoundedCornerShape(8.dp),

                title = {

                    Column {

                        Text(
                            text = "Send Code",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "A verification Code was sent to your email",
                            color = Color.Gray,
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Code sent to ${state.identifier}",
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                    }
                },

                text = {

                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        OutlinedTextField(
                            value = code,
                            onValueChange = {
                                if (it.all(Char::isDigit)) {
                                    code = it
                                }
                            },
                            label = {
                                Text("Forgot Password Code")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )

                    }
                },

                confirmButton = {

                    Button(
                        onClick = {

                            // send verification code
                            viewModel.confirmCode(code)

                        },
                        enabled =
                        code.isNotBlank(),

                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2E7D32),
                            contentColor = Color.White
                        )
                    ) {

                        Text(
                            text = "Verify Code",
                            fontWeight = FontWeight.Bold
                        )
                    }
                },

                dismissButton = {

                    OutlinedButton(
                        onClick = {
                            viewModel.closeCodeDialog()
                        },
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF990000),
                            contentColor = Color.White
                        )
                    ) {

                        Text(
                            "Cancel",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )
        }

        if(passwordDialog){

            AlertDialog(

                onDismissRequest = {
                    viewModel.closePasswordDialog()
                },
                shape = RoundedCornerShape(8.dp),

                title = {

                    Column {

                        Text(
                            text = "Reset Password",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Complete the details below",
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                    }
                },

                text = {

                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        OutlinedTextField(
                            value = password,
                            onValueChange = {
                                password = it
                            },
                            label = {
                                Text("New Password")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            visualTransformation = if (passwordVisible) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            },
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        passwordVisible = !passwordVisible }

                                ) {
                                    Icon(
                                        painter = painterResource(
                                            id = if (passwordVisible)
                                                R.drawable.visibility_off_24px
                                            else
                                                R.drawable.visibility_24px
                                        ),
                                        contentDescription = "Toggle PIN visibility",
                                        tint = Color.Gray
                                    )
                                }
                            }
                        )

                        OutlinedTextField(
                            value = passwordConfirm,
                            onValueChange = {
                                passwordConfirm = it
                            },
                            label = {
                                Text("Confirm New Password")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            visualTransformation = if (passwordConfirmVisible) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            },
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        passwordConfirmVisible = !passwordConfirmVisible }

                                ) {
                                    Icon(
                                        painter = painterResource(
                                            id = if (passwordConfirmVisible)
                                                R.drawable.visibility_off_24px
                                            else
                                                R.drawable.visibility_24px
                                        ),
                                        contentDescription = "Toggle PIN visibility",
                                        tint = Color.Gray
                                    )
                                }
                            }
                        )
                    }
                },

                confirmButton = {

                    Button(
                        onClick = {

                            // reset password
                            if(password == passwordConfirm){
                                viewModel.resetPassword(password)
                            }else{
                                // show password dont match
                            }

                        },
                        enabled =
                        password.isNotBlank() && passwordConfirm.isNotBlank() && password == passwordConfirm,

                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2E7D32),
                            contentColor = Color.White
                        )
                    ) {

                        Text(
                            text = "Reset Password",
                            fontWeight = FontWeight.Bold
                        )
                    }
                },

                dismissButton = {

                    OutlinedButton(
                        onClick = {
                            viewModel.closePasswordDialog()
                        },
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF990000),
                            contentColor = Color.White
                        )
                    ) {

                        Text(
                            "Cancel",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )
        }

        if(resetSuccessful){

            AlertDialog(

                onDismissRequest = {},
                shape = RoundedCornerShape(8.dp),

                title = {

                    Column {
                        Text(
                            text = "Password changed successfully",
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                    }
                },

                confirmButton = {

                    Button(
                        onClick = {
                            navController.navigate("login")
                            viewModel.closeResetSuccess()
                        },

                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2E7D32),
                            contentColor = Color.White
                        )
                    ) {

                        Text(
                            text = "Go to Login Screen",
                            fontWeight = FontWeight.Bold
                        )
                    }
                },

            )
        }

        state.error?.let {
            DismissFailureDialog(
                showDialog = requestError,
                message = it,
                onDismiss = { viewModel.closeRequestError()})
        }

    }
}

