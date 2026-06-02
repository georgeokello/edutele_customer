package com.example.edutelecustomer.ui.screens.historyscreen


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.edutelecustomer.data.local.UserPreferences
import com.example.edutelecustomer.data.model.transactions.TransactionItem
import com.example.edutelecustomer.ui.components.AppTemplate
import com.example.edutelecustomer.ui.components.AppTemplateViewModel
import com.example.edutelecustomer.ui.components.AppTemplateViewModelFactory
import com.example.edutelecustomer.ui.navigation.navItems
import com.example.edutelecustomer.ui.util.navigateTo

@RequiresApi(Build.VERSION_CODES.O)
@Composable

fun HistoryScreen(navController: NavController) {

    val currentRoute = "history"

    val context = LocalContext.current

    val userPreferences = UserPreferences(context)

    val viewModel: HistoryViewModel = viewModel(
        factory = HistoryViewModelFactory(userPreferences)
    )

    val appViewModel: AppTemplateViewModel = viewModel(
        factory = AppTemplateViewModelFactory(userPreferences)
    )

    val cardInfo by appViewModel.cardInfoUiState.collectAsState()

    val user by viewModel.username.collectAsState()


    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchTransactions()
    }

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
            Spacer(modifier = Modifier.height(50.dp))
            Text(text = "Transaction History")
            Spacer(modifier = Modifier.height(5.dp))
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {

                    when {
                        state.isLoading -> {
                            CircularProgressIndicator()
                        }

                        state.error != null -> {
                            LaunchedEffect(state.error) {

                                if (state.error == "Invalid or expired token.") {

                                    navController.navigate("login") {
                                        popUpTo(0)
                                        launchSingleTop = true
                                    }
                                }
                            }
                            Text(
                                text = "${state.error}",
                                color = Color.Gray
                            )
                        }

                        else -> {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(state.transactions) { transaction ->
                                    TransactionCard(transaction, viewModel)
                                }
                            }
                        }
                    }
                }
            }
        }

    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransactionCard(
    transaction: TransactionItem,
    viewModel: HistoryViewModel
) {

    val statusText = when (transaction.status.lowercase()) {
        "posted" -> "Completed"
        "pending" -> "Pending"
        "failed" -> "Failed"
        else -> transaction.status
    }

    val statusColor = when (transaction.status.lowercase()) {
        "posted" -> Color(0xFF2E7D32)
        "pending" -> Color(0xFFF9A825)
        "failed" -> Color(0xFFC62828)
        else -> Color.Gray
    }

    val statusBackground = when (transaction.status.lowercase()) {
        "posted" -> Color(0xFFE8F5E9)
        "pending" -> Color(0xFFFFF8E1)
        "failed" -> Color(0xFFFFEBEE)
        else -> Color(0xFFF5F5F5)
    }

    val amountColor = if (transaction.amount.trim().startsWith("-")) {
        Color(0xFF990000) // red
    } else {
        Color(0xFF16A34A) // green (or your default)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(1.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {

        Column(
            modifier = Modifier.padding(15.dp)
        ) {

            // TOP ROW
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column {

                    Text(
                        text = transaction.type.uppercase(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = viewModel.formatTimestamp(transaction.timestamp),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = statusBackground
                ) {

                    Text(
                        text = statusText.uppercase(),
                        modifier = Modifier.padding(
                            horizontal = 14.dp,
                            vertical = 6.dp
                        ),
                        color = statusColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // AMOUNT
            Text(
                text = "UGX ${transaction.amount}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = amountColor
            )

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(color = Color(0xFFEAEAEA))

            Spacer(modifier = Modifier.height(12.dp))

            // BALANCE AFTER
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = "New Balance",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Text(
                    text = "UGX ${transaction.balance_after}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
            }
        }
    }
}