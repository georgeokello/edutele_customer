package com.eduteleaccess.customer.ui.screens.qrcode

import com.eduteleaccess.customer.data.local.UserPreferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eduteleaccess.customer.data.repository.QrCodeRespository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONObject

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

                    val errorBody = response.errorBody()?.string()

                    val errorDetail = errorBody?.let {
                        try {
                            JSONObject(it).getString("detail")
                        } catch (e: Exception) {
                            "Unknown error"
                        }
                    }

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = errorDetail
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
