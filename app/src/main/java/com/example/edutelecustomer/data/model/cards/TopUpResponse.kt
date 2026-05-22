package com.example.edutelecustomer.data.model.cards

data class TopUpResponse(
    val amount: String,
    val currency: String,
    val remarks: String,
    val status: String,
)

