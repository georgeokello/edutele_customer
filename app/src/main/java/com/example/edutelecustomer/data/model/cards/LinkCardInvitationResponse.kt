package com.example.edutelecustomer.data.model.cards

data class LinkCardInvitationResponse(
    val items : List<LinkCardInvitationItems>,
    val total: Int
)

data class LinkCardInvitationItems(
    val relationship_id: Int,
    val parent_public_id: String,
    val parent_name: String,
    val parent_phone: String,
    val relationship_type: String,
    val can_topup: Boolean,
    val can_view_balance: Boolean,
    val can_view_history: Boolean,
    val can_freeze: Boolean,
    val requested_at: String
)