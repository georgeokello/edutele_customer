package com.example.edutelecustomer.ui.screens.homescreen

import com.example.edutelecustomer.data.local.UserPreferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edutelecustomer.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val userPreferences: UserPreferences,
    private val repository: AuthRepository
) : ViewModel() {
    private val _amount = MutableStateFlow("")
    val amount: StateFlow<String> get() = _amount


    val token: StateFlow<String?> =
        userPreferences.tokenFlow
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )

    val username: StateFlow<String?> =
        userPreferences.usernameFlow
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )



}