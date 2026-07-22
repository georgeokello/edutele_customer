package com.eduteleaccess.customer.ui.screens.historyscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eduteleaccess.customer.data.local.UserPreferences
import com.eduteleaccess.customer.data.remote.RetrofitInstance
import com.eduteleaccess.customer.data.repository.RedemptionHistoryRepository

class HistoryViewModelFactory(
    private val userPreferences: UserPreferences
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        val api = RetrofitInstance.api
        val repository = RedemptionHistoryRepository(api)

        return HistoryViewModel(userPreferences, repository) as T

    }
}