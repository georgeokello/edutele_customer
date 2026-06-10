package com.example.edutelecustomer.data.model.sendaccess

data class SendAccessResponse(

    val public_id : String,
    val from_customer_public_id: String,
    val to_customer_public_id: String,
    val amount: String,
    val currency: String,
    val remarks: String,
    val status: String,
    val created_at: String,
    val completed_at: String

)