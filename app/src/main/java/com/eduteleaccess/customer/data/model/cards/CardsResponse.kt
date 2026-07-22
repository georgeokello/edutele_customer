package com.eduteleaccess.customer.data.model.cards


data class CardsResponse(
    val items: List<RelationshipItem>,
    val total: Int
)

data class RelationshipItem(
    val public_id: String,
    val full_name: String,
    val phone: String,
    val account_type: String,
    val has_card: Boolean,
    val card_frozen: Boolean,
    val on_my_membership: Boolean,
    val i_manage_them: RelationshipDetails,
    val they_manage_me: RelationshipDetails
)

data class RelationshipDetails(
    val status: String,
    val relationship_id: Int,
    val relationship_type: String,
    val can_allocate: Boolean,
    val can_view_remaining: Boolean,
    val can_view_history: Boolean,
    val can_freeze: Boolean,
    val weekly_spend_limit: String,
    val linked_at: String
)