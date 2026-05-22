package com.example.edutelecustomer.ui.util

fun extractInitials(name: String): String {

    return name
        .trim()
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") {
            it.first().uppercase()
        }
}