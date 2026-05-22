package com.example.edutelecustomer.ui.screens.qrcode

import android.graphics.Bitmap
import android.graphics.Color
import com.example.edutelecustomer.data.local.UserPreferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edutelecustomer.data.repository.QrCodeRespository
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class UiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val payToken: String? = null
)

class QrCodeViewModel(
    private val userPreferences: UserPreferences,
    private val repository: QrCodeRespository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(UiState())

    val uiState: StateFlow<UiState> =
        _uiState

    val username: StateFlow<String?> =
        userPreferences.usernameFlow
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )

    fun generatePayToken() {

        viewModelScope.launch {

            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            try {

                val tokenAuth =
                    userPreferences.tokenFlow.first()

                if (tokenAuth.isNullOrBlank()) {

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Authentication token missing"
                    )

                    return@launch
                }

                val response =
                    repository.generateQrCode(tokenAuth)

                if (response.isSuccessful) {

                    val token =
                        response.body()?.qr_data

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        payToken = token
                    )

                } else {

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = response.errorBody()?.string()
                            ?: "Failed to generate token"
                    )
                }

            } catch (e: Exception) {

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Network error"
                )
            }
        }
    }
}
