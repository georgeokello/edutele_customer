package com.example.edutelecustomer.ui.screens.sendmoneyscreen

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.edutelecustomer.data.local.UserPreferences
import com.example.edutelecustomer.data.model.sendmoney.PhoneLookUpResponse
import com.example.edutelecustomer.data.repository.SendMoneyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONObject

data class SendMoneyUiState(
    val isLoading : Boolean = false,
    val publicId: String = "",
    val fullName: String = "",
    val message: String = "",
    val error: String? = null
)

class SendMoneyViewModel (
    private val userPreferences: UserPreferences,
    private val repository: SendMoneyRepository
) : ViewModel(){

    var amountDialog = MutableStateFlow(false)
        private set

    var successDialog = MutableStateFlow(false)
        private set

    var uiState = MutableStateFlow(SendMoneyUiState())
        private set

    val username: StateFlow<String?> =
        userPreferences.usernameFlow
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )

    fun lookUpNumber(phone: String){
        viewModelScope.launch {
            Log.e("SEND_ERROR", "Inside viewModel Scope now")
            try{
                uiState.value = uiState.value.copy(
                    isLoading = true,
                    error = null
                )
                Log.e("SEND_ERROR", "Inside viewModel try block now")
                val tokenValue =
                    userPreferences.tokenFlow.first()
                if(!tokenValue.isNullOrEmpty()){
                    Log.e("SEND_ERROR", "check token successful")
                    val response = repository.lookUpPhoneNumber(tokenValue, phone)
                    if(response.isSuccessful){
                        Log.e("SEND_ERROR", "response successful")
                        openAmountDialog()
                        uiState.value = response.body()?.let {
                            uiState.value.copy(
                                publicId = it.public_id,
                                fullName = it.full_name,
                                isLoading = false
                            )
                        }!!
                    }else{
                        val errorBody = response.errorBody()?.string()

                        val errorDetail = errorBody?.let {
                            try {
                                JSONObject(it).getString("detail")
                            } catch (e: Exception) {
                                "Unknown error"
                            }
                        }

                        uiState.value = uiState.value.copy(
                            error = errorDetail,
                            isLoading = false

                        )

                        Log.e("SEND_ERROR", "response failed")
                    }
                }else{
                    uiState.value = uiState.value.copy(
                        error = "No token found",
                        isLoading = false

                    )
                    Log.e("SEND_ERROR", "No token found")
                }
            }catch (e:Exception){
                uiState.value = uiState.value.copy(
                    isLoading = false,
                    error = "Try again Later"
                )
                Log.e("SEND_ERROR", "Inside catch $e")
            }
        }
    }

    fun sendMoney(publicId: String, amount: String, remarks:String, pin:String){
        viewModelScope.launch {
            try{
                uiState.value = uiState.value.copy(
                    isLoading = true,
                    error = null
                )
                val tokenValue =
                    userPreferences.tokenFlow.first()
                if(!tokenValue.isNullOrEmpty()){
                    val response = repository.sendMoney(tokenValue, publicId, amount,remarks, pin)
                    if(response.isSuccessful){

                        uiState.value = response.body()?.let {
                            uiState.value.copy(
                                isLoading = false
                            )
                        }!!

                        closeAmountDialog()
                        openSuccessDialog()
                    }else{
                        val errorBody = response.errorBody()?.string()

                        val errorDetail = errorBody?.let {
                            try {
                                JSONObject(it).getString("detail")
                            } catch (e: Exception) {
                                "Unknown error"
                            }
                        }

                        uiState.value = uiState.value.copy(
                            error = errorDetail,
                            isLoading = false

                        )
                        closeAmountDialog()

                    }
                }else{
                    uiState.value = uiState.value.copy(
                        error = "No token found",
                        isLoading = false

                    )
                    Log.e("SEND_ERROR", "No token found")
                }
            }catch (e:Exception){
                uiState.value = uiState.value.copy(
                    isLoading = false,
                    error = "Try again Later"
                )
            }
        }
    }

    fun openAmountDialog(){
        amountDialog.value = true
    }

    fun closeAmountDialog(){
        amountDialog.value = false
    }

    fun openSuccessDialog(){
        successDialog.value = true
    }

    fun closeSuccessDialog(){
        successDialog.value = false
    }
}