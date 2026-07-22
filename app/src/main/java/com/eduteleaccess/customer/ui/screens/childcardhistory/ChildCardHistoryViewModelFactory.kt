package com.eduteleaccess.customer.ui.screens.childcardhistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eduteleaccess.customer.data.local.UserPreferences
import com.eduteleaccess.customer.data.remote.RetrofitInstance
import com.eduteleaccess.customer.data.repository.CardsRepository

class ChildCardHistoryViewModelFactory(private val userPreferences: UserPreferences): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val api = RetrofitInstance.api
        val repository = CardsRepository(api)
        return ChildCardHistoryViewModel(userPreferences, repository) as T
    }
}