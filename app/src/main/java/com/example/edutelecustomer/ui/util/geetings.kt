package com.example.edutelecustomer.ui.util

fun getGreeting(userName: String): String {

    val hour = java.util.Calendar.getInstance()
        .get(java.util.Calendar.HOUR_OF_DAY)

    val greeting = when (hour) {

        in 0..11 -> "Good morning"

        in 12..16 -> "Good afternoon"

        in 17..20 -> "Good evening"

        else -> "Good night"
    }

    return "$greeting, $userName"
}