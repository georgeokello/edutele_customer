package com.example.edutelecustomer.data.model.sendmoney

data class SendMoneyRequest (
    val recipient_public_id: String,
    val amount : Int,
    val pin: String,
    val remarks: String
)