package com.eduteleaccess.customer.ui.screens.homescreen


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eduteleaccess.customer.data.local.UserPreferences
import com.eduteleaccess.customer.data.repository.AuthRepository

class HomeViewModelFactory(
    private val userPreferences: UserPreferences,
    private val repository: AuthRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(userPreferences, repository) as T
    }
}