package com.eduteleaccess.customer.ui.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eduteleaccess.customer.data.local.UserPreferences
import com.eduteleaccess.customer.data.remote.RetrofitInstance
import com.eduteleaccess.customer.data.repository.AppTemplateRepository

class AppTemplateViewModelFactory(
    private val userPreferences: UserPreferences
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val api = RetrofitInstance.api
        val repository = AppTemplateRepository(api)
        return AppTemplateViewModel(userPreferences,repository) as T
    }
}