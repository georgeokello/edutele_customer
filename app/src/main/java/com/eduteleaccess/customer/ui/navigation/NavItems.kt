package com.eduteleaccess.customer.ui.navigation


import androidx.annotation.DrawableRes
import androidx.compose.material.icons.filled.*
import com.eduteleaccess.customer.R

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