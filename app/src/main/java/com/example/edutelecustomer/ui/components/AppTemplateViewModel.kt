package com.example.edutelecustomer.ui.components

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edutelecustomer.data.local.UserPreferences
import com.example.edutelecustomer.data.model.cards.CardInfo
import com.example.edutelecustomer.data.model.cards.QuickStats
import com.example.edutelecustomer.data.model.cards.RecentRedemptions
import com.example.edutelecustomer.data.model.cards.Rewards
import com.example.edutelecustomer.data.model.cards.UsageBreakdown
import com.example.edutelecustomer.data.repository.AppTemplateRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONObject


data class CardInfoUiState(
    val isLoading: Boolean = false,

    val card: CardInfo? = null,

    val quickStats: QuickStats? = null,

    val recentRedemptions: List<RecentRedemptions> = emptyList(),

    val usageBreakdown: List<UsageBreakdown> = emptyList(),

    val hasMoreRedemptions: Boolean = false,

    val rewards: Rewards? = null,

    val error: String? = null
)

class AppTemplateViewModel(
    private val userPreferences: UserPreferences,
    private val repository: AppTemplateRepository): ViewModel() {

    private val _cardInfoUiState = MutableStateFlow(CardInfoUiState())
    val cardInfoUiState = _cardInfoUiState.asStateFlow()

    var logoutState = MutableStateFlow(false)
        private set


    init {
        getCardInfo()
    }

    fun getCardInfo() {

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
                            recentRedemptions = response.body()!!.recent_access_events,
                            usageBreakdown = response.body()!!.spending_breakdown,
                            hasMoreRedemptions = response.body()!!.has_more_access_events,
                            rewards = response.body()!!.rewards,
                            error = null
                        )
                    } else {
                        // handle error/ request unsuccessful

                        val errorBody = response.errorBody()?.string()

                        val errorDetail = errorBody?.let {
                            try {
                                JSONObject(it).getString("detail")
                            } catch (e: Exception) {
                                "Unknown error, Try again Later"
                            }
                        }

                        _cardInfoUiState.value = _cardInfoUiState.value.copy(
                            error = errorDetail,
                            isLoading = false
                        )
                    }

                }else{
                    _cardInfoUiState.value = _cardInfoUiState.value.copy(
                        error = "No token found",
                        isLoading = false
                    )
                }

            } catch (e: Exception) {

                _cardInfoUiState.value = _cardInfoUiState.value.copy(
                    error = "Try again Later",
                    isLoading = false
                )
            }
        }
    }

    fun logout(){
        viewModelScope.launch {
            try {
                val tokenValue =
                    userPreferences.tokenFlow.first()

                if(!tokenValue.isNullOrEmpty()){
                    val response = repository.logout(tokenValue)
                    if (response.isSuccessful){
                        logoutState.value = true
                    } else {
                        logoutState.value = false
                    }

                }

            }catch(e:Exception){
                logoutState.value = false
            }
        }
    }
}