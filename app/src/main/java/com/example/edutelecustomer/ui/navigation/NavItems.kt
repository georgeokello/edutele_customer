package com.example.edutelecustomer.ui.navigation


import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.example.edutelecustomer.R

data class BottomNavItem(
    val label: String,
    @DrawableRes val icon: Int,
    val route: String
)

val navItems = listOf(
    BottomNavItem(
        "HOME",
        R.drawable.home_24px,
        "home"),
    BottomNavItem(
        "QrCODE",
        R.drawable.qr_code_24px,
        "qrCode"),
    BottomNavItem(
        "CARDS",
        R.drawable.credit_card_24px,
        "cards"),
    BottomNavItem(
        "HISTORY",
        R.drawable.receipt_long_24px,
        "history")

)