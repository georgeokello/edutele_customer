package com.example.edutelecustomer.ui.screens.sendmoneyscreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.edutelecustomer.R
import com.example.edutelecustomer.data.local.UserPreferences
import com.example.edutelecustomer.data.model.transactions.TransactionItem
import com.example.edutelecustomer.ui.components.AppTemplate
import com.example.edutelecustomer.ui.components.AppTemplateViewModel
import com.example.edutelecustomer.ui.components.AppTemplateViewModelFactory
import com.example.edutelecustomer.ui.navigation.navItems
import com.example.edutelecustomer.ui.screens.historyscreen.HistoryViewModel
import com.example.edutelecustomer.ui.screens.historyscreen.HistoryViewModelFactory
import com.example.edutelecustomer.ui.util.navigateTo


@Composable
fun SendMoneyScreen(navController: NavController) {

    val currentRoute = "send"

    val context = LocalContext.current

    val userPreferences = UserPreferences(context)

    val viewModel: SendMoneyViewModel = viewModel(
        factory = SendMoneyViewModelFactory(userPreferences)
    )

    val appViewModel: AppTemplateViewModel = viewModel(
        factory = AppTemplateViewModelFactory(userPreferences)
    )

    val cardInfo by appViewModel.cardInfoUiState.collectAsState()

    val user by viewModel.username.collectAsState()

    var phoneNumber by remember { mutableStateOf("") }


    //val state by viewModel.uiState.collectAsState()


    AppTemplate(
        userName = user.toString(),
        balance = " ${cardInfo.card?.balance ?: 0}",
        navItems = navItems,
        selectedNavIndex = navItems.indexOfFirst { it.route == currentRoute },
        onNavSelected = { index ->
            val route = navItems[index].route
            if (route != currentRoute) navigateTo(navController, route)
        }
    ) {
        Column {

            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        tonalElevation = 4.dp,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                            // HEADER
                                Text(
                                    text = "Send Money",
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF0B2C5F)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Enter recipient phone number",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                // INPUT CARD STYLE FIELD
                                OutlinedTextField(
                                    value = phoneNumber,
                                    onValueChange = {
                                        if (it.all { ch -> ch.isDigit() }) {
                                            phoneNumber = it
                                        }
                                    },
                                    label = { Text("Phone Number") },
                                    placeholder = { Text("07XXXXXXXX") },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Phone,
                                            contentDescription = null
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(60.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Number
                                    )
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                // SEND BUTTON
                                Button(
                                    onClick = {

                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp),
                                    enabled = phoneNumber.length >= 9,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF2E7D32),
                                        contentColor = Color.White,
                                        disabledContainerColor = Color(0xFFBDBDBD)
                                    ),
                                    shape = RoundedCornerShape(14.dp)
                                ) {

                                    Icon(
                                        painter = painterResource(id = R.drawable.send_money_24px),
                                        contentDescription = null
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Text(
                                        text = "Send Money",
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                        }
                    }
                }

                // TOP RIGHT (Example action button)
                FloatingActionButton(
                    onClick = {
                        navigateTo(navController, "home")
                    },
                    containerColor = Color(0xFF990000),
                    contentColor = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 35.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Back"
                    )
                }
            }
        }

    }
}
