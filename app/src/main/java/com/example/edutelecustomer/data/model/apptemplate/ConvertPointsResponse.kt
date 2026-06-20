package com.example.edutelecustomer.data.model.apptemplate

data class ConvertPointsResponse(
    val points_redeemed : Int,
    val ugx_added_to_credit: String,
    val points_remaining: Int,
    val redemption_credit: String
)