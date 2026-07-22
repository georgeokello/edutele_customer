package com.eduteleaccess.customer.data.model.sendaccess

data class SendAccessRequest (
    val recipient_public_id: String,
    val amount : Int,
    val pin: String,
    val remarks: String
)