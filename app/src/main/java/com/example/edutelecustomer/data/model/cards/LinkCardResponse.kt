package com.example.edutelecustomer.data.model.cards

data class LinkCardResponse(
    val id: Int,
    val parent_customer_id: Int,
    val child_customer_id: Int,
    val relationship_type: String,
    val link_status: String,
    val can_topup: Boolean,
    val can_view_balance: Boolean,
    val can_view_history: Boolean,
    val can_freeze: Boolean,
    val is_active: Boolean,
    val created_at: String,
    val revoked_at: String
)