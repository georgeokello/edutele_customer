package com.example.edutelecustomer.ui.screens.historyscreen


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edutelecustomer.data.local.UserPreferences
import com.example.edutelecustomer.data.model.redemptions.RedemptionItem
import com.example.edutelecustomer.data.repository.RedemptionHistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

// 📊 UI State
data class RedemptionUiState(
    val isLoading: Boolean = false,
    val redemption: List<RedemptionItem> = emptyList(),
    val error: String? = null
)

class HistoryViewModel(
    private val userPreferences: UserPreferences,
    private val repository: RedemptionHistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RedemptionUiState())
    val uiState: StateFlow<RedemptionUiState> = _uiState

    val username: StateFlow<String?> =
        userPreferences.usernameFlow
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )

    fun fetchRedemptions() {
        viewModelScope.launch {

            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            try {
                val tokenValue =
                    userPreferences.tokenFlow.first()

                if(!tokenValue.isNullOrEmpty()){

                    val response = repository.getCustomerRedemptions(tokenValue)

                    if (response.isSuccessful) {

                        val redemptions =
                            response.body()?.items ?: emptyList()

                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            redemption = redemptions
                        )

                    } else {

                        val errorBody = response.errorBody()?.string()

                        val errorDetail = errorBody?.let {
                            try {
                                JSONObject(it).getString("detail")
                            } catch (e: Exception) {
                                "Unknown error, Try again Later"
                            }
                        }

                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = errorDetail
                        )
                    }

                }else{
                    _uiState.value = _uiState.value.copy(
                        error = "No token found"
                    )
                }


            } catch (e: Exception) {

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Something went wrong, Try again Later"
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatTimestamp(timestamp: String): String {

        val instant = Instant.parse(timestamp)

        val localDateTime = instant.atZone(
            ZoneId.systemDefault()
        )

        val formatter = DateTimeFormatter.ofPattern(
            "dd MMM yyyy, h:mm a"
        )

        return localDateTime.format(formatter)
    }
}