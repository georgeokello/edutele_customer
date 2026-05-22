package com.example.edutelecustomer.ui.screens.loginscreen


import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.edutelecustomer.data.local.UserPreferences
import com.example.edutelecustomer.data.repository.AuthRepository
import com.example.edutelecustomer.data.remote.RetrofitInstance


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