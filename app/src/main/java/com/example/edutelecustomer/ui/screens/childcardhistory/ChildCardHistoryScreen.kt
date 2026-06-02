package com.example.edutelecustomer.ui.screens.childcardhistory

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.edutelecustomer.R
import com.example.edutelecustomer.data.local.UserPreferences
import com.example.edutelecustomer.data.model.cards.ChildHistoryItems
import com.example.edutelecustomer.ui.components.AppTemplate
import com.example.edutelecustomer.ui.components.AppTemplateViewModel
import com.example.edutelecustomer.ui.components.AppTemplateViewModelFactory
import com.example.edutelecustomer.ui.navigation.navItems
import com.example.edutelecustomer.ui.util.navigateTo


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChildCardHistoryScreen(navController: NavController, path: String) {

    val currentRoute = "childCardHistory/${path}"

    val context = LocalContext.current

    val userPreferences = UserPreferences(context)

    val viewModel: ChildCardHistoryViewModel = viewModel(
        factory = ChildCardHistoryViewModelFactory(userPreferences)
    )

    val appViewModel: AppTemplateViewModel = viewModel(
        factory = AppTemplateViewModelFactory(userPreferences)
    )

    val cardInfo by appViewModel.cardInfoUiState.collectAsState()

    val user by viewModel.username.collectAsState()


    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchChildCardTransactions(path)
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
        Box(modifier = Modifier.fillMaxSize()){
            Column {
                Spacer(modifier = Modifier.height(30.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "Card Transactions",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    FloatingActionButton(
                        onClick = {
                            navigateTo(navController, "cards")
                        },
                        containerColor = Color(0xFF990000),
                        contentColor = Color.White,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Back"
                        )
                    }

                }
                Spacer(modifier = Modifier.height(10.dp))
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
                            items(state.childCardHistory) { transaction ->
                                TransactionCard(transaction, viewModel)
                            }
                        }
                    }
                }

            }

            // TOP RIGHT (Example action button)

        }

    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransactionCard(
    transaction: ChildHistoryItems,
    viewModel: ChildCardHistoryViewModel
) {
    val statusText = when (transaction.status?.lowercase()) {
        "posted" -> "Completed"
        "pending" -> "Pending"
        "failed" -> "Failed"
        else -> transaction.status
    }

    val statusColor = when (transaction.status?.lowercase()) {
        "posted" -> Color(0xFF2E7D32)
        "pending" -> Color(0xFFF9A825)
        "failed" -> Color(0xFFC62828)
        else -> Color.Gray
    }

    val statusBackground = when (transaction.status?.lowercase() ?: "") {
        "posted" -> Color(0xFFE8F5E9)
        "pending" -> Color(0xFFFFF8E1)
        "failed" -> Color(0xFFFFEBEE)
        else -> Color(0xFFF5F5F5)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {

            // TOP ROW
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column {

                    transaction.tx_type?.let {
                        Text(
                            text = it.uppercase(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = viewModel.formatTimestamp(transaction.created_at ?: ""),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = statusBackground
                ) {

                    Text(
                        text = statusText ?: "",
                        color = statusColor,
                        modifier = Modifier.padding(
                            horizontal = 14.dp,
                            vertical = 6.dp
                        ),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // AMOUNT
            Text(
                text = "UGX ${transaction.amount}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = statusColor
            )

            Spacer(modifier = Modifier.height(14.dp))

            HorizontalDivider(
                color = Color(0xFFEAEAEA)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // DESCRIPTION
            Text(
                text = "Reason",
                fontSize = 12.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = transaction.description ?: "",
                fontSize = 13.sp,
                color = Color.DarkGray,
                lineHeight = 20.sp
            )
        }
    }
}