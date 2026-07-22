package com.eduteleaccess.customer.data.repository

import android.util.Log
import com.eduteleaccess.customer.data.local.UserPreferences
import com.eduteleaccess.customer.data.model.forgotpassword.codeRequest
import com.eduteleaccess.customer.data.model.forgotpassword.codeResponse
import com.eduteleaccess.customer.data.model.forgotpassword.newPasswordRequest
import com.eduteleaccess.customer.data.model.forgotpassword.newPasswordResponse
import com.eduteleaccess.customer.data.model.forgotpassword.requestCodeRequest
import com.eduteleaccess.customer.data.model.forgotpassword.requestCodeResponse
import com.eduteleaccess.customer.data.model.login.LoginRequest
import com.eduteleaccess.customer.data.remote.ApiService
import org.json.JSONObject
import retrofit2.Response


class AuthRepository(
    private val api: ApiService,
    private val userPreferences: UserPreferences
) {

    var user_token = ""
    var customer_id = ""

    suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            val response = api.login(LoginRequest(email, password))
            Log.d("LOGIN_Direct", "User ID: ${response.body()}")
            if (response.isSuccessful) {
                //store token, and userID
                user_token = response.body()!!.access_token
                customer_id = response.body()!!.customer_id
                val response2 = api.getUserDetails("Bearer ${response.body()!!.access_token}")
                if (response2.isSuccessful){
                    // save username/ vendor Id
                    userPreferences.saveUser(
                        token = user_token,
                        customerId = customer_id,
                        full_name = response2.body()!!.last_name,
                    )
                    Result.success(Unit)

                }else{
                    Result.failure(Exception("Me failed"))
                }

            } else {
                val errorBody = response.errorBody()?.string()

                val errorDetail = errorBody?.let {
                    try {
                        JSONObject(it).getString("detail")
                    } catch (e: Exception) {
                        "Unknown error, Try again Later"
                    }
                }
                Result.failure(Exception(errorDetail))
            }

        } catch (e: Exception) {
            Result.failure(Exception("Internet connection issue. Please check your network and try again."))
        }
    }

    suspend fun requestResetCode(email: String): Response<requestCodeResponse>{
        return api.requestForgotPasswordCode(
            requestCodeRequest(email)
        )
    }

    suspend fun confirmCode(identifier: String,code: String): Response<codeResponse>{
        return api.confirmForgotPasswordCode(
            codeRequest(identifier,code)
        )
    }

    suspend fun confirmNewPassword(token: String,password: String): Response<newPasswordResponse>{
        return api.confirmNewPassword(
            newPasswordRequest(token, password)
        )
    }

}