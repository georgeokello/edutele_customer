package com.eduteleaccess.customer.ui.screens.childcardhistory

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eduteleaccess.customer.data.local.UserPreferences
import com.eduteleaccess.customer.data.model.cards.ChildHistoryItems
import com.eduteleaccess.customer.data.repository.CardsRepository
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
data class ChildHistoryUiState(
    val isLoading: Boolean = false,
    val childCardHistory: List<ChildHistoryItems> = emptyList(),
    val error: String? = null
)

class ChildCardHistoryViewModel(
    private val userPreferences: UserPreferences,
    private val repository: CardsRepository): ViewModel() {


    private val _uiState = MutableStateFlow(ChildHistoryUiState())
    val uiState: StateFlow<ChildHistoryUiState> = _uiState

    val username: StateFlow<String?> =
        userPreferences.usernameFlow
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )

    fun fetchChildCardHistory(child_public_id: String) {

        viewModelScope.launch {

            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            try {
                val tokenValue =
                    userPreferences.tokenFlow.first()

                if(!tokenValue.isNullOrEmpty()){

                    Log.d("Inside Fetch Function", child_public_id)

                    val response = repository.getChildCardHistory(tokenValue, child_public_id)

                    if (response.isSuccessful) {

                        val childCardHistories =
                            response.body()?.items ?: emptyList()

                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            childCardHistory =  childCardHistories
                        )

                        Log.d("Inside Response Success", "it ran")

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

                e.message?.let { Log.d("Inside Catch", it) }
            }
        }
    }


    fun formatTimestamp(timestamp: String): String {
        return try {
            val instant = Instant.parse(timestamp)

            val localDateTime = instant.atZone(ZoneId.systemDefault())

            val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, h:mm a")

            localDateTime.format(formatter)

        } catch (e: Exception) {
            "Invalid date"
        }
    }
}