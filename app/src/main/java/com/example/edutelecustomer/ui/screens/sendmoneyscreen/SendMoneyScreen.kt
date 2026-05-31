package com.example.edutelecustomer.ui.screens.sendmoneyscreen

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
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


@SuppressLint("StateFlowValueCalledInComposition")
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

    val uiState by viewModel.uiState.collectAsState()

    val user by viewModel.username.collectAsState()

    var phoneNumber by remember { mutableStateOf("") }

    var amount by remember { mutableStateOf("") }
    var remarks by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }


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
                    )
                    {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                            )
                        {
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
                                        viewModel.lookUpNumber(phoneNumber)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .padding(),
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
                            Spacer(modifier = Modifier.height(8.dp))

                            when {
                                uiState.isLoading -> {
                                    CircularProgressIndicator()
                                }

                                uiState.error != null -> {
                                    LaunchedEffect(cardInfo.error) {

                                        if (uiState.error == "Invalid or expired token.") {

                                            navController.navigate("login") {
                                                popUpTo(0)
                                                launchSingleTop = true
                                            }
                                        }
                                    }
                                    Text(
                                        text = "${uiState.error}",
                                        color = Color.Gray
                                    )
                                }
                            }

                            if(viewModel.amountDialog.value){

                                    AlertDialog(

                                        onDismissRequest = {
                                            viewModel.closeAmountDialog()
                                        },

                                        title = {

                                            Column {

                                                Text(
                                                    text = "Send Money",
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

                                                // Recipient card
                                                Card(
                                                    colors = CardDefaults.cardColors(
                                                        containerColor = Color(0xFFF5F7FA)
                                                    ),
                                                    shape = RoundedCornerShape(12.dp)
                                                ) {

                                                    Column(
                                                        modifier = Modifier.padding(12.dp)
                                                    ) {

                                                        Text(
                                                            text = "Recipient",
                                                            color = Color.Gray,
                                                            fontSize = 12.sp
                                                        )

                                                        Text(
                                                            text = viewModel.uiState.value.fullName,
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 16.sp
                                                        )

                                                        Text(
                                                            text = phoneNumber,
                                                            color = Color.Gray
                                                        )
                                                    }
                                                }

                                                OutlinedTextField(
                                                    value = amount,
                                                    onValueChange = {
                                                        if (it.all(Char::isDigit)) {
                                                            amount = it
                                                        }
                                                    },
                                                    label = {
                                                        Text("Amount (UGX)")
                                                    },
                                                    modifier = Modifier.fillMaxWidth(),
                                                    keyboardOptions = KeyboardOptions(
                                                        keyboardType = KeyboardType.Number
                                                    ),
                                                    singleLine = true
                                                )

                                                OutlinedTextField(
                                                    value = remarks,
                                                    onValueChange = {
                                                        remarks = it
                                                    },
                                                    label = {
                                                        Text("Remarks")
                                                    },
                                                    modifier = Modifier.fillMaxWidth(),
                                                    maxLines = 2
                                                )

                                                OutlinedTextField(
                                                    value = pin,
                                                    onValueChange = {
                                                        if (it.all(Char::isDigit) && it.length <= 6) {
                                                            pin = it
                                                        }
                                                    },
                                                    label = {
                                                        Text("PIN")
                                                    },
                                                    modifier = Modifier.fillMaxWidth(),
                                                    visualTransformation = PasswordVisualTransformation(),
                                                    keyboardOptions = KeyboardOptions(
                                                        keyboardType = KeyboardType.NumberPassword
                                                    ),
                                                    singleLine = true
                                                )
                                            }
                                        },

                                        confirmButton = {

                                            Button(
                                                onClick = {

                                                    // send money
                                                    viewModel.SendMoney(viewModel.uiState.value.publicId, amount, remarks, pin)
                                                },
                                                enabled =
                                                amount.isNotBlank() &&
                                                        pin.length >= 4,

                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = Color(0xFF2E7D32),
                                                    contentColor = Color.White
                                                )
                                            ) {

                                                Text(
                                                    text = "Send Money",
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        },

                                        dismissButton = {

                                            OutlinedButton(
                                                onClick = {
                                                    viewModel.closeAmountDialog()
                                                }
                                            ) {

                                                Text("Cancel")
                                            }
                                        }
                                    )
                                }

                            if(viewModel.successDialog.value){
                                AlertDialog(

                                    onDismissRequest = {
                                        viewModel.closeSuccessDialog()
                                    },

                                    icon = {

                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = Color(0xFF2E7D32),
                                            modifier = Modifier.size(64.dp)
                                        )
                                    },

                                    title = {

                                        Text(
                                            text = "Transfer Successful",
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    },

                                    text = {

                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {

                                            Text(
                                                text = "Your money has been sent successfully.",
                                                color = Color.Gray
                                            )

                                            Card(
                                                shape = RoundedCornerShape(12.dp),
                                                colors = CardDefaults.cardColors(
                                                    containerColor = Color(0xFFF5F7FA)
                                                )
                                            ) {

                                                Column(
                                                    modifier = Modifier.padding(16.dp),
                                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {

                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                    ) {

                                                        Text(
                                                            text = "Recipient",
                                                            color = Color.Gray
                                                        )

                                                        Text(
                                                            text = viewModel.uiState.value.fullName,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }

                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                    ) {

                                                        Text(
                                                            text = "Amount",
                                                            color = Color.Gray
                                                        )

                                                        Text(
                                                            text = "UGX $amount",
                                                            fontWeight = FontWeight.Bold,
                                                            color = Color(0xFF2E7D32)
                                                        )
                                                    }

                                                }
                                            }
                                        }
                                    },

                                    confirmButton = {

                                        Button(
                                            onClick = {
                                                // dismiss dialog
                                                viewModel.closeSuccessDialog()
                                            },
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFF2E7D32),
                                                contentColor = Color.White
                                            )
                                        ) {

                                            Text(
                                                text = "Done",
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
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
