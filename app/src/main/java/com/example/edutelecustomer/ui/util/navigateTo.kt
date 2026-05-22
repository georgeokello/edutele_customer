package com.example.edutelecustomer.ui.util


import androidx.navigation.NavController

import androidx.navigation.NavGraph.Companion.findStartDestination

fun navigateTo(
    navController: NavController,
    route: String
) {
    navController.navigate(route) {
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}