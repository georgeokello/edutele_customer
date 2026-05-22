package com.example.edutelecustomer.data.model.cards

data class ChildCardBalanceResponse(
    val child_public_id: String,
    val full_name: String,
    val card_number: String,
    val balance: String,
    val has_card: Boolean
)
