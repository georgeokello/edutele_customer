package com.example.edutelecustomer.ui.screens.homescreen


import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.edutelecustomer.R
import com.example.edutelecustomer.data.local.UserPreferences
import com.example.edutelecustomer.data.model.cards.CardInfo
import com.example.edutelecustomer.data.model.cards.RecentTransaction
import com.example.edutelecustomer.data.repository.AuthRepository
import com.example.edutelecustomer.data.remote.RetrofitInstance
import com.example.edutelecustomer.data.repository.AppTemplateRepository
import com.example.edutelecustomer.ui.components.AppTemplate
import com.example.edutelecustomer.ui.components.AppTemplateViewModel
import com.example.edutelecustomer.ui.components.AppTemplateViewModelFactory
import com.example.edutelecustomer.ui.components.CardInfoUiState
import com.example.edutelecustomer.ui.navigation.navItems
import com.example.edutelecustomer.ui.screens.childcardhistory.TransactionCard
import com.example.edutelecustomer.ui.util.navigateTo


@RequiresApi(Build.VERSION_CODES.O)
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

    val cardInfo by appViewModel.cardInfoUiState.collectAsState()

    val user by viewModel.username.collectAsState()



    val currentRoute = "home"

    LaunchedEffect(Unit) {
        appViewModel.getCardInfo()
    }

    AppTemplate(
        userName=user.toString(),
        balance = " ${cardInfo.card?.balance ?: "0"}",
        navItems = navItems,
        selectedNavIndex = navItems.indexOfFirst { it.route == currentRoute },
        onNavSelected = { index ->
            val route = navItems[index].route
            if (route != currentRoute) navigateTo(navController, route)
        }
    ) {
        Box(Modifier.fillMaxSize()) {

            Column() {
                Spacer(modifier = Modifier.height(45.dp))
                FinanceStatCard(
                    belowText = "Total Spending",
                    points = cardInfo.rewards?.points_balance ?: 0,
                    badge = cardInfo.rewards?.badge?.name ?: "--",
                    number = cardInfo.quickStats?.total_spent?.value ?: "--",
                    percentage = (cardInfo.quickStats?.total_spent?.change_pct ?: "--").toString()
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
                        text = "Recent Transactions",
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
                            text = "Transfer Access",
                            fontWeight = FontWeight.Bold
                        )
                    }

                }

                when {
                    cardInfo.isLoading -> {
                        CircularProgressIndicator()
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
                            items(cardInfo.recentTransactions) { transaction ->
                                RecentHistory(transaction)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FinanceStatCard(
    belowText: String,
    number: String,
    percentage: String,
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
                text = percentage,
                modifier = Modifier.align(Alignment.TopEnd),
                color = percentageColor,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
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

                Surface(
                    shape = RoundedCornerShape(35),
                    color = when (badge) {
                        "Gold" -> Color(0xFFD4AF37)
                        "Silver" -> Color(0xFFB0BEC5)
                        else -> Color(0xFFCD7F32)
                    }
                ) {

                    Text(
                        text = badge,
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 4.dp
                        ),
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
fun RecentHistory(recentTransaction: RecentTransaction) {

    val statusColor = when (recentTransaction.status.lowercase()) {
        "success", "completed", "paid" -> Color(0xFF16A34A)
        "pending" -> Color(0xFFF59E0B)
        "failed", "declined" -> Color(0xFF990000)
        else -> Color(0xFF6B7280)
    }

    val amountColor = if (recentTransaction.amount.trim().startsWith("-")) {
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
                    text = recentTransaction.activity,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF111827)
                )

                Text(
                    text = recentTransaction.amount,
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
                        text = recentTransaction.place,
                        fontSize = 13.sp,
                        color = Color(0xFF6B7280)
                    )

                    Text(
                        text = recentTransaction.date,
                        fontSize = 12.sp,
                        color = Color(0xFF9CA3AF)
                    )
                }

                Text(
                    text = recentTransaction.status.uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = statusColor
                )
            }
        }
    }
}