package com.example.edutelecustomer.data.model.cards

data class LinkChildRequest(
    val child_phone: String,
    val relationship_type: String,
    val can_topup: Boolean,
    val can_view_balance: Boolean,
    val can_view_history: Boolean,
    val can_freeze: Boolean

)
