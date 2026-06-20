package com.example.edutelecustomer.ui.screens.homescreen


import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.painter.Painter
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
import com.example.edutelecustomer.data.model.cards.RecentRedemptions
import com.example.edutelecustomer.data.repository.AuthRepository
import com.example.edutelecustomer.data.remote.RetrofitInstance
import com.example.edutelecustomer.ui.components.AppTemplate
import com.example.edutelecustomer.ui.components.AppTemplateViewModel
import com.example.edutelecustomer.ui.components.AppTemplateViewModelFactory
import com.example.edutelecustomer.ui.navigation.navItems
import com.example.edutelecustomer.ui.util.DismissFailureDialog
import com.example.edutelecustomer.ui.util.DismissSuccessDialog
import com.example.edutelecustomer.ui.util.navigateTo
import kotlinx.coroutines.flow.MutableStateFlow


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val userPreferences = remember { UserPreferences(context) }

    val api = RetrofitInstance.api // however you provide it
    val repository = remember { AuthRepository(api, userPreferences) }

    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(userPreferences, repository)
    )

    val appViewModel: AppTemplateViewModel = viewModel(
        factory = AppTemplateViewModelFactory(userPreferences)
    )

    val convertPointsDialog by appViewModel.convertPointsDialog.collectAsState()

    val cardInfo by appViewModel.cardInfoUiState.collectAsState()

    val user by viewModel.username.collectAsState()
    val successDialog by appViewModel.setSuccessDialog.collectAsState()
    val failureDialog by appViewModel.setFailureDialog.collectAsState()
    val dialogMessage by appViewModel.dialogMessage.collectAsState()

    val pointsWorthPerUgx = cardInfo.rewards?.points_per_ugx ?: 0

    var pointsValue by remember { mutableStateOf("") }



    val currentRoute = "home"

    LaunchedEffect(Unit) {
        appViewModel.getCardInfo()
    }

    AppTemplate(
        userName=user.toString(),
        availableAccess = " ${cardInfo.card?.remaining ?: "0"}",
        navItems = navItems,
        selectedNavIndex = navItems.indexOfFirst { it.route == currentRoute },
        onNavSelected = { index ->
            val route = navItems[index].route
            if (route != currentRoute) navigateTo(navController, route)
        },
        navController = navController
    ) {
        Box(Modifier.fillMaxSize()) {

            Column() {
                Spacer(modifier = Modifier.height(45.dp))
                AccessStatCard(
                    appViewModel,
                    belowText = "Total Usage",
                    points = cardInfo.rewards?.points_remaining ?: 0,
                    badge = cardInfo.rewards?.badge?.name ?: "--",
                    number = cardInfo.quickStats?.total_spent?.value ?: "--",
                    converted = (cardInfo.rewards?.redemption_credit ?: "--").toString()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "Recent Redemptions",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Button(
                        onClick = {
                            navigateTo(navController, "send")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2E7D32),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.send_money_24px),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Text(
                            text = "Send Access",
                            fontWeight = FontWeight.Bold
                        )
                    }

                }

                when {
                    cardInfo.isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                    }

                    cardInfo.error != null -> {
                        LaunchedEffect(cardInfo.error) {

                            if (cardInfo.error == "Invalid or expired token.") {

                                navController.navigate("login") {
                                    popUpTo(0)
                                    launchSingleTop = true
                                }
                            }
                        }
                        Text(
                            text = "${cardInfo.error}",
                            color = Color.Gray
                        )
                    }

                    else -> {

                        LazyColumn(
                        ) {
                            items(cardInfo.recentRedemptions) { redemption ->
                                RecentAccess(redemption)
                            }
                        }
                    }
                }
                if(convertPointsDialog){
                    AlertDialog(

                        onDismissRequest = {
                            appViewModel.closeConvertPointsDialog()
                        },
                        shape = RoundedCornerShape(8.dp),

                        title = {

                            Column {

                                Text(
                                    text = "Convert Points",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "$pointsWorthPerUgx = 1 Access",
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
                                    shape = RoundedCornerShape(8.dp)
                                ) {

                                }

                                OutlinedTextField(
                                    value = pointsValue,
                                    onValueChange = {
                                        if (it.all(Char::isDigit)) {
                                            pointsValue = it
                                        }
                                    },
                                    label = {
                                        Text("Enter points")
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
                                    appViewModel.closeConvertPointsDialog()
                                    // Handle confirmation
                                    appViewModel.convertPoints(pointsValue)

                                },
                                enabled =
                                pointsValue.isNotBlank(),

                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF2E7D32),
                                    contentColor = Color.White
                                )
                            ) {

                                Text(
                                    text = "Convert",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        },

                        dismissButton = {

                            OutlinedButton(
                                onClick = {
                                    appViewModel.closeConvertPointsDialog()
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
                DismissSuccessDialog(
                    showDialog = successDialog,
                    message = dialogMessage,
                    onDismiss = {
                        appViewModel.closeSuccessDialog()
                    }
                )

                DismissFailureDialog(
                    showDialog = failureDialog,
                    message = dialogMessage,
                    onDismiss = {
                        appViewModel.closeFailureDialog()
                    }
                )
            }
        }
    }
}

@Composable
fun AccessStatCard(
    appTemplateViewModel: AppTemplateViewModel,
    belowText: String,
    number: String,
    converted: String,
    modifier: Modifier = Modifier,
    points: Int = 5000,
    badge: String = "Silver",
    amountColor: Color = Color(0xFF0B2C5F),
    percentageColor: Color = Color(0xFF00A63E),
    iconBackground: Color = Color(0xFFF2F4F8),
    iconTint: Color = Color(0xFFCE8D00),
    containerColor: Color = Color.White,
    icon: Painter = painterResource(id = R.drawable.account_balance_wallet_24px)
) {

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp),

        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp)
        ) {

            Icon(
                painter = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .size(22.dp)
            )

            // TOP RIGHT
            Text(
                text = "Rewards: $converted Ugx",
                modifier = Modifier.align(Alignment.TopEnd),
                color = percentageColor,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )

            // BOTTOM LEFT
            Column(
                modifier = Modifier.align(Alignment.BottomStart)
            ) {
                Text(
                    text = number,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = amountColor
                )

                Text(
                    text = belowText,
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280)
                )
            }

            // BOTTOM RIGHT (FIXED)
            Column(
                modifier = Modifier.align(Alignment.BottomEnd),
                horizontalAlignment = Alignment.End
            ) {

                Text(
                    text = "$points pts",
                    color = Color(0xFFCE8D00),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = { appTemplateViewModel.openConvertPointsDialog() },
                    modifier = Modifier.height(28.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = when (badge) {
                            "Gold" -> Color(0xFFCE8D00)
                            "Silver" -> Color(0xFF6B7280)
                            else -> Color(0xFF012A56)
                        }
                    ),
                    contentPadding = PaddingValues(
                        horizontal = 10.dp,
                        vertical = 0.dp
                    )
                ) {
                    Text(
                        text = "Convert",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

            }
        }
    }
}

@Composable
fun RecentAccess(recentRedemptions: RecentRedemptions) {

    val statusColor = when (recentRedemptions.status.lowercase()) {
        "success", "completed", "paid" -> Color(0xFF16A34A)
        "pending" -> Color(0xFFF59E0B)
        "failed", "declined" -> Color(0xFF990000)
        else -> Color(0xFF6B7280)
    }

    val amountColor = if (recentRedemptions.amount.trim().startsWith("-")) {
        Color(0xFF990000) // red
    } else {
        Color(0xFF16A34A) // green (or your default)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3E4E6)),
    ) {

        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
        ) {

            // TOP ROW
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = recentRedemptions.activity,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF111827)
                )

                Text(
                    text = recentRedemptions.amount,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = amountColor
                )
            }

            // "HR" DIVIDER
            Spacer(modifier = Modifier.height(8.dp))

            HorizontalDivider(
                thickness = 1.dp,
                color = Color(0xFFCFCFCF)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // BOTTOM ROW
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column {

                    Text(
                        text = recentRedemptions.place,
                        fontSize = 13.sp,
                        color = Color(0xFF6B7280)
                    )

                    Text(
                        text = recentRedemptions.date,
                        fontSize = 12.sp,
                        color = Color(0xFF9CA3AF)
                    )
                }

                Text(
                    text = recentRedemptions.status.uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = statusColor
                )
            }
        }
    }
}