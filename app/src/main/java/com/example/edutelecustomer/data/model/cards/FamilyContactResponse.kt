package com.example.edutelecustomer.data.model.cards

data class FamilyContactResponse (
    val items: List<FamilyContactItems>,
    val total: Int
)

data class FamilyContactItems(
    val requester_public_id: String,
    val full_name: String,
    val phone: String,
    val requested_at: String
)