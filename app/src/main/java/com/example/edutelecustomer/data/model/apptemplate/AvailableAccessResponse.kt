package com.example.edutelecustomer.data.model.apptemplate

data class AvailableAccessResponse(
    val items: List<CardItem>,
    val total: Int
)

data class CardItem(
    val card_uid: String,
    val network_id: Int,
    val card_number: String,
    val status: String,
    val nfc_uid: String,
    val is_nfc_bound: Boolean,
    val balance: String,
    val total_topups: String,
    val total_spent: String,
    val total_refunds: String,
    val issued_at: String,
    val expiry: String,
    val last_transaction_at: String,
    val last_topup_at: String,
    val customer_public_id: String
)