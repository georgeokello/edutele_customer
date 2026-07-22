package com.eduteleaccess.customer.data.model.forgotpassword

data class newPasswordRequest (
    val token: String,
    val password: String,
)