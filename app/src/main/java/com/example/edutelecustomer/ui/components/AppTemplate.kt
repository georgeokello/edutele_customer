package com.example.edutelecustomer.ui.components


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.edutelecustomer.R
import com.example.edutelecustomer.ui.navigation.BottomNavItem
import com.example.edutelecustomer.ui.util.getGreeting


@Composable
fun AppTemplate(
    userName: String = "Daniel",
    balanceTitle: String = "Balance",
    balance: String = "UGX 250,000",
    balanceBelowText: String = "Wallet Balance",
    navItems: List<BottomNavItem>,
    selectedNavIndex: Int,
    onNavSelected: (Int) -> Unit,
    navController: NavController,
    content: @Composable () -> Unit
) {
    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                items = navItems,
                selectedIndex = selectedNavIndex,
                onItemClick = onNavSelected
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFEDEDED))
        ) {

            Column {
                // Header Section
                HeaderSection(userName, navController)

                Spacer(modifier = Modifier.height(5.dp)) // space for floating card

                // Content Area
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFEDEDED))
                        .padding(16.dp)
                ) {
                    content()
                }
            }

            // Floating Balance Card
            BalanceCard(
                balanceTitle= balanceTitle,
                balance = balance,
                balanceBelowText = balanceBelowText,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = 180.dp)
                    .padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
fun HeaderSection(userName: String, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxHeight(0.35f)
            .fillMaxWidth()
            .background(Color(0xFF012A56))
            .padding(top = 50.dp, bottom = 40.dp, start = 16.dp, end = 16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(65.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(color = Color.White)) {
                            append("EDUTELE ")
                        }
                        withStyle(style = SpanStyle(color = Color(0xFFCE8D00))) {
                            append("ACCESS")
                        }
                    },
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 15.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column {
                Text(
                    text = getGreeting(userName),
                    color = Color.White,
                    fontSize = 16.sp,
                )
            }

            OutlinedButton(
                onClick = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                modifier = Modifier.height(32.dp),
                contentPadding = PaddingValues(
                    horizontal = 8.dp,
                    vertical = 0.dp
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White,
                    containerColor = Color(0xFF990000)
                ),
                border = BorderStroke(
                    1.dp,
                    Color(0xFF990000)
                ),
                shape = RoundedCornerShape(10.dp),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "Logout",
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun BalanceCard(
    balanceTitle: String,
    balance: String,
    balanceBelowText: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(0.18f),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(1.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0156A6)
        )
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(10.dp)


        ) {
            Text(
                text = balanceTitle,
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier.padding(6.dp)
            )

            Text(
                text = balance,
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(5.dp)
            )

            Text(
                text = balanceBelowText,
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier.padding(6.dp)
            )
        }
    }
}

@Composable
fun BottomNavigationBar(
    items: List<BottomNavItem>,
    selectedIndex: Int,
    onItemClick: (Int) -> Unit
) {
    NavigationBar(
        containerColor = Color(0xFF012A56)
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = index == selectedIndex,
                onClick = { onItemClick(index) },
                icon = {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = item.label,
                        tint = if (index == selectedIndex) Color(0xFFCE8D00) else Color.Gray
                    )
                },
                label = {
                    Text(
                        item.label,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (index == selectedIndex) Color(0xFFCE8D00) else Color.Gray
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}