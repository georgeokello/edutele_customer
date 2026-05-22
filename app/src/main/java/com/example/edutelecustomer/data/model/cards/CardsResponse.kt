package com.example.edutelecustomer.data.model.cards

data class CardsResponse(
    val items: List<LinkedCardItem>,
    val total: Int
)

data class LinkedCardItem(
    val child_public_id: String,
    val full_name: String,
    val phone: String,
    val account_type: String,
    val relationship_type: String,
    val link_status: String,
    val can_topup: Boolean,
    val can_view_balance: Boolean,
    val can_view_history: Boolean,
    val can_freeze: Boolean,
    val card_frozen_by_parent: Boolean,
    val weekly_spend_limit: String,
    val linked_at: String
)