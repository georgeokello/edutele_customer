package com.eduteleaccess.customer.data.model.forgotpassword

data class codeResponse (
    val reset_token: String,
    val expires_in: Int
)