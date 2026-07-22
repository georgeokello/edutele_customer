package com.eduteleaccess.customer.data.model.cards

data class ChildCardAvailableAccessResponse(
    val child_public_id: String,
    val full_name: String,
    val card_number: String,
    val remaining: String,
    val has_card: Boolean
)
