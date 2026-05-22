package com.example.edutelecustomer.data.model.login

data class LoginDetailsResponse(
    val access_token: String,
    val customer_id: String,
    val full_name: String,
    val role: String,
    val status: String
)