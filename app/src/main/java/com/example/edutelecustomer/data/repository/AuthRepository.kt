package com.example.edutelecustomer.data.repository

import android.util.Log
import com.example.edutelecustomer.data.local.UserPreferences
import com.example.edutelecustomer.data.model.login.LoginRequest
import com.example.edutelecustomer.data.remote.ApiService
import org.json.JSONObject


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

}