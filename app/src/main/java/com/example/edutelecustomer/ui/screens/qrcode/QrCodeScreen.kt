package com.example.edutelecustomer.ui.screens.qrcode


import android.annotation.SuppressLint

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.edutelecustomer.data.local.UserPreferences

import com.example.edutelecustomer.ui.components.AppTemplate
import com.example.edutelecustomer.ui.components.AppTemplateViewModel
import com.example.edutelecustomer.ui.components.AppTemplateViewModelFactory
import com.example.edutelecustomer.ui.navigation.navItems
import com.example.edutelecustomer.ui.util.generateQrImage
import com.example.edutelecustomer.ui.util.navigateTo


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun QrCodeScreen(
    navController: NavController
) {

    val currentRoute = "qrCode"
    val context = LocalContext.current
    val userPreferences = remember {
        UserPreferences(context)
    }

    val viewModel: QrCodeViewModel = viewModel(
        factory =QrCodeViewModelFactory(userPreferences)
    )

    val appViewModel: AppTemplateViewModel = viewModel(
        factory = AppTemplateViewModelFactory(userPreferences)
    )

    val cardInfo by appViewModel.cardInfoUiState.collectAsState()

    val user by viewModel.username.collectAsState()

    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.generatePayToken()
    }

    AppTemplate(
        userName=user.toString(),
        accessTitle = "",
        availableAccess = "Scan the QR Code",
        accessBelowText = "",
        navItems = navItems,
        selectedNavIndex = navItems.indexOfFirst { it.route == currentRoute },
        onNavSelected = { index ->
            val route = navItems[index].route
            if (route != currentRoute) navigateTo(navController, route)
        },
        navController = navController
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                }

                state.error != null -> {
                    LaunchedEffect(state.error) {

                        if (cardInfo.error == "Invalid or expired token.") {

                            navController.navigate("login") {
                                popUpTo(0)
                                launchSingleTop = true
                            }
                        }
                    }
                    Text(
                        text = "Something went wrong, Check your internet",
                        color = Color.Gray
                    )
                }

                else -> {
                    state.payToken?.let { token ->

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            QrCode(token)

                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QrCode(token: String) {

    val bitmap = remember {
        generateQrImage(token)
    }

    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = "QR Code",
        modifier = Modifier.size(250.dp)
    )
}

