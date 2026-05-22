package com.example.edutelecustomer.ui.navigation


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

val navItems = listOf(
    BottomNavItem("HOME", Icons.Default.Home, "home"),
    BottomNavItem("QrCODE", Icons.Default.Settings, "qrCode"),
    BottomNavItem("CARDS", Icons.Default.AccountCircle, "cards"),
    BottomNavItem("HISTORY", Icons.Default.DateRange, "history")

)