package com.eduteleaccess.customer.data.model.cards

data class TopUpRequest (
    val amount: Int,
    val pin: String,
    val remarks: String
)
