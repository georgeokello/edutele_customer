package com.example.edutelecustomer.ui.screens.sendaccessscreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edutelecustomer.data.local.UserPreferences
import com.example.edutelecustomer.data.repository.SendAccessRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONObject

data class SendAccessUiState(
    val isLoading : Boolean = false,
    val publicId: String = "",
    val fullName: String = "",
    val message: String = "",
    val error: String? = null
)

class SendAccessViewModel (
    private val userPreferences: UserPreferences,
    private val repository: SendAccessRepository
) : ViewModel(){

    var accessValueDialog = MutableStateFlow(false)
        private set

    var successDialog = MutableStateFlow(false)
        private set

    var uiState = MutableStateFlow(SendAccessUiState())
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
            try{
                uiState.value = uiState.value.copy(
                    isLoading = true,
                    error = null
                )
                val tokenValue =
                    userPreferences.tokenFlow.first()
                if(!tokenValue.isNullOrEmpty()){
                    val response = repository.lookUpPhoneNumber(tokenValue, phone)
                    if(response.isSuccessful){
                        openAccessValueDialog()
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

                    }
                }else{
                    uiState.value = uiState.value.copy(
                        error = "No token found",
                        isLoading = false

                    )
                }
            }catch (e:Exception){
                uiState.value = uiState.value.copy(
                    isLoading = false,
                    error = "Try again Later"
                )
            }
        }
    }

    fun sendAccess(publicId: String, accessValue: String, remarks:String, pin:String){
        viewModelScope.launch {
            try{
                uiState.value = uiState.value.copy(
                    isLoading = true,
                    error = null
                )
                val tokenValue =
                    userPreferences.tokenFlow.first()
                if(!tokenValue.isNullOrEmpty()){
                    val response = repository.sendAccess(tokenValue, publicId, accessValue,remarks, pin)
                    if(response.isSuccessful){

                        uiState.value = response.body()?.let {
                            uiState.value.copy(
                                isLoading = false
                            )
                        }!!

                        closeAccessValueDialog()
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
                        closeAccessValueDialog()

                    }
                }else{
                    uiState.value = uiState.value.copy(
                        error = "No token found",
                        isLoading = false

                    )
                }
            }catch (e:Exception){
                uiState.value = uiState.value.copy(
                    isLoading = false,
                    error = "Try again Later"
                )
            }
        }
    }

    fun openAccessValueDialog(){
        accessValueDialog.value = true
    }

    fun closeAccessValueDialog(){
        accessValueDialog.value = false
    }

    fun openSuccessDialog(){
        successDialog.value = true
    }

    fun closeSuccessDialog(){
        successDialog.value = false
    }
}