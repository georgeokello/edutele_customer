package com.example.edutelecustomer.ui.screens.loginscreen


import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.edutelecustomer.R

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
) {

    val context = LocalContext.current
    val viewModel: LoginViewModel = viewModel(
        factory = LoginViewModelFactory(context)
    )

    val state by viewModel.uiState.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F8FB)),
        contentAlignment = Alignment.Center
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
                    value = state.email,
                    onValueChange = viewModel::onEmailChange,
                    label = { Text(
                        "Email, Phone or Code",
                        color = Color.Gray
                    ) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                // PASSWORD
                OutlinedTextField(
                    value = state.password,
                    onValueChange = viewModel::onPasswordChange,
                    label = { Text(
                        "Password",
                        color = Color.Gray
                    ) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    visualTransformation =
                    if (passwordVisible)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),

                    trailingIcon = {
                        IconButton(
                            onClick = {
                                passwordVisible = !passwordVisible
                            }
                        ) {
                            Icon(
                                painter = painterResource(
                                    id = if (passwordVisible)
                                        R.drawable.visibility_off_24px
                                    else
                                        R.drawable.visibility_24px
                                ),
                                contentDescription = null,
                                tint = Color.Gray
                            )
                        }
                    }
                )

                // LOGIN BUTTON
                Button(
                    onClick = viewModel::login,
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
                            text = "Login",
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
                            // navigate to forgot password screen

                            val url = "https://eduteleaccess.com/forgot-password/card-holder"
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = android.net.Uri.parse(url)
                            }
                            context.startActivity(intent)

                        }
                    ) {
                        Text(
                            "Forgot password?",
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
}

