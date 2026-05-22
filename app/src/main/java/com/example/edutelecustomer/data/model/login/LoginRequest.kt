package com.example.edutelecustomer.data.model.login

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("identifier")
    val identifier: String,
    val password: String
)