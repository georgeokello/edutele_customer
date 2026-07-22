package com.eduteleaccess.customer.ui.screens.forgotpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eduteleaccess.customer.data.model.cards.ChildHistoryItems
import com.eduteleaccess.customer.data.repository.AuthRepository
import com.eduteleaccess.customer.ui.screens.childcardhistory.ChildHistoryUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject


// 📊 UI State
data class UiState(
    val isLoading: Boolean = false,
    val resetToken: String = "",
    val identifier: String =" ",
    val error: String? = null
)

class ForgotViewModel(
    private val repository: AuthRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    var codeDialog = MutableStateFlow(false)
        private set

    var passwordDialog = MutableStateFlow(false)
        private set

    var resetSuccessful = MutableStateFlow(false)
        private set

    var requestError = MutableStateFlow(false)
        private set

    // request reset password code
    fun requestResetCode(email: String){
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                identifier = email,
                error = null
            )

            try {
                val response = repository.requestResetCode(email)
                if(response.isSuccessful){
                    _uiState.value = _uiState.value.copy(
                        //resetToken = ,
                        isLoading = false,
                    )
                    // launch code AlertDialog
                    openCodeDialog()

                }else{
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

                    requestError.value = true
                }
            }catch (e:Exception){
                requestError.value = true
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Something went wrong, Try again Later"
                )
            }
        }
    }

    // confirm forgot password code
    fun confirmCode(code: String){
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            try {
                val response = repository.confirmCode(_uiState.value.identifier,code)
                if(response.isSuccessful){
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        resetToken = response.body()?.reset_token ?: ""
                    )
                    // close code Dialog
                    closeCodeDialog()
                    // launch confirm password AlertDialog
                    openPasswordDialog()
                }else{
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
                    requestError.value = true
                }
            }catch (e:Exception){
                requestError.value = true
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Something went wrong, Try again Later"
                )
            }

        }
    }

    // Reset password
    fun resetPassword(newPassword: String){
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            val resetToken = _uiState.value.resetToken

            try {
                val response = repository.confirmNewPassword(resetToken, newPassword)
                if(response.isSuccessful){
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                    )
                    // navigate to the login screen
                    resetSuccessful.value = true
                    // close confirm password AlertDialog
                    closePasswordDialog()

                }else{
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

                    requestError.value = true
                }
            }catch (e:Exception){
                requestError.value = true
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Something went wrong, Try again Later"
                )
            }
        }
    }


    private fun openCodeDialog(){
        codeDialog.value = true
    }

    fun closeCodeDialog(){
        codeDialog.value = false
    }

    fun openPasswordDialog(){
        passwordDialog.value = true
    }

    fun closePasswordDialog(){
        passwordDialog.value = false
    }

    fun closeResetSuccess(){
        resetSuccessful.value = false
    }

    fun closeRequestError(){
        requestError.value = false
    }

}

