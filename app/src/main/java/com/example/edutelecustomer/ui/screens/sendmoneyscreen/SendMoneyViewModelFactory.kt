package com.example.edutelecustomer.ui.screens.sendmoneyscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.edutelecustomer.data.local.UserPreferences
import com.example.edutelecustomer.data.remote.RetrofitInstance
import com.example.edutelecustomer.data.repository.SendMoneyRepository

class SendMoneyViewModelFactory (
    private val userPreferences: UserPreferences
): ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val api = RetrofitInstance.api
        val repository = SendMoneyRepository(api)
        return SendMoneyViewModel(userPreferences, repository) as T
    }

}