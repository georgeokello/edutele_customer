package com.example.edutelecustomer.ui.screens.childcardhistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.edutelecustomer.data.local.UserPreferences
import com.example.edutelecustomer.data.remote.RetrofitInstance
import com.example.edutelecustomer.data.repository.CardsRepository

class ChildCardHistoryViewModelFactory(private val userPreferences: UserPreferences): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val api = RetrofitInstance.api
        val repository = CardsRepository(api)
        return ChildCardHistoryViewModel(userPreferences, repository) as T
    }
}