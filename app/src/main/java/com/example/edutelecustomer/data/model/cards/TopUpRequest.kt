package com.example.edutelecustomer.data.model.cards

data class TopUpRequest (
    val amount: Int,
    val pin: String,
    val remarks: String
)
