package com.example.edutelecustomer.data.model.cards

data class ChildCardHistoryResponse (
    val items: List<ChildHistoryItems>,
    val total: Int
)

data class ChildHistoryItems(
    val public_id: String,
    val tx_type: String,
    val amount: String,
    val status: String,
    val description: String,
    val created_at: String
)
