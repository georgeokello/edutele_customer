package com.eduteleaccess.customer.ui.screens.forgotpassword

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eduteleaccess.customer.data.local.UserPreferences
import com.eduteleaccess.customer.data.remote.RetrofitInstance
import com.eduteleaccess.customer.data.repository.AuthRepository
import com.eduteleaccess.customer.ui.screens.loginscreen.LoginViewModel

class ForgotViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val api = RetrofitInstance.api
        val prefs = UserPreferences(context)
        val repository = AuthRepository(api, prefs)

        return ForgotViewModel(repository) as T
    }
}