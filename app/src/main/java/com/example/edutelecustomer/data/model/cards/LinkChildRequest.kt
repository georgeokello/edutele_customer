package com.example.edutelecustomer.data.model.cards

data class LinkChildRequest(
    val child_public_id: String,
    val relationship_type: String,
    val can_allocate: Boolean,
    val can_view_remaining: Boolean,
    val can_view_history: Boolean,
    val can_freeze: Boolean

)
