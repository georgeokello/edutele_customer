package com.example.edutelecustomer.ui.screens.homescreen


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.edutelecustomer.data.local.UserPreferences
import com.example.edutelecustomer.data.repository.AuthRepository

class HomeViewModelFactory(
    private val userPreferences: UserPreferences,
    private val repository: AuthRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(userPreferences, repository) as T
    }
}