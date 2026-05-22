package com.example.edutelecustomer.ui.components

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edutelecustomer.data.local.UserPreferences
import com.example.edutelecustomer.data.model.cards.CardInfo
import com.example.edutelecustomer.data.model.cards.QuickStats
import com.example.edutelecustomer.data.model.cards.RecentTransaction
import com.example.edutelecustomer.data.model.cards.SpendingBreakdown
import com.example.edutelecustomer.data.repository.AppTemplateRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


data class CardInfoUiState(
    val isLoading: Boolean = false,

    val card: CardInfo? = null,

    val quickStats: QuickStats? = null,

    val recentTransactions: List<RecentTransaction> = emptyList(),

    val spendingBreakdown: List<SpendingBreakdown> = emptyList(),

    val error: String? = null
)

class AppTemplateViewModel(
    private val userPreferences: UserPreferences,
    private val repository: AppTemplateRepository): ViewModel() {

    private val _cardInfoUiState = MutableStateFlow(CardInfoUiState())
    val cardInfoUiState = _cardInfoUiState.asStateFlow()


    init {
        getCardInfo()
    }

    private fun getCardInfo() {

        viewModelScope.launch {

            _cardInfoUiState.value = _cardInfoUiState.value.copy(
                isLoading = true
            )


            try {

                val tokenValue =
                    userPreferences.tokenFlow.first()

                if(!tokenValue.isNullOrEmpty()){
                    val response = repository.getCardInfo(tokenValue)
                    if (response.isSuccessful){
                        _cardInfoUiState.value = CardInfoUiState(
                            isLoading = false,
                            card = response.body()?.card,
                            quickStats = response.body()?.quick_stats,
                            recentTransactions = response.body()!!.recent_transactions,
                            spendingBreakdown = response.body()!!.spending_breakdown,
                            error = null
                        )
                    } else {
                        // handle error/ request unsuccessful
                        _cardInfoUiState.value = _cardInfoUiState.value.copy(
                            error = "failed to get card Info"
                        )
                    }

                }else{
                    _cardInfoUiState.value = _cardInfoUiState.value.copy(
                        error = "No token found"
                    )
                }

            } catch (e: Exception) {

                _cardInfoUiState.value = _cardInfoUiState.value.copy(
                    error = e.message
                )
            }
        }
    }
}