package com.eduteleaccess.customer.ui.screens.loginscreen


data class LogUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)