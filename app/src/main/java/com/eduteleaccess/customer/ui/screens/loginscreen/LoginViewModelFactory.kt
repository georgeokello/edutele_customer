package com.eduteleaccess.customer.ui.screens.loginscreen


import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eduteleaccess.customer.data.local.UserPreferences
import com.eduteleaccess.customer.data.repository.AuthRepository
import com.eduteleaccess.customer.data.remote.RetrofitInstance


class LoginViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val api = RetrofitInstance.api
        val prefs = UserPreferences(context)
        val repository = AuthRepository(api, prefs)

        return LoginViewModel(repository) as T
    }
}