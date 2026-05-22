package com.example.edutelecustomer.data.model.transactions


data class TransactionsResponse(
    val items: List<TransactionItem>,
    val total: Int
)

data class TransactionItem(
    val public_id: String,
    val type: String,
    val status: String,
    val amount: String,
    val balance_before: String,
    val balance_after: String,
    val reference: String,
    val remarks: String,
    val timestamp: String,
    val verified_at: String,
    val fee_amount: String,
    val gross_amount: String
)