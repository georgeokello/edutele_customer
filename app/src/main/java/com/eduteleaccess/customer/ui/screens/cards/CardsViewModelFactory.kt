package com.eduteleaccess.customer.ui.screens.cards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import com.eduteleaccess.customer.data.local.UserPreferences
import com.eduteleaccess.customer.data.remote.RetrofitInstance
import com.eduteleaccess.customer.data.repository.CardsRepository

class CardsViewModelFactory(private val userPreferences: UserPreferences): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        val api = RetrofitInstance.api
        val repository = CardsRepository(api)

        return CardsViewModel(userPreferences, repository) as T
    }
}