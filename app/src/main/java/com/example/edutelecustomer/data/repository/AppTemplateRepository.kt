package com.example.edutelecustomer.data.repository


import com.example.edutelecustomer.data.model.apptemplate.LogoutResponse
import com.example.edutelecustomer.data.model.cards.CardInfoResponse
import com.example.edutelecustomer.data.remote.ApiService
import retrofit2.Response

class AppTemplateRepository(
    private val api: ApiService
) {
    suspend fun getCardInfo(token: String): Response<CardInfoResponse>{
        return api.getCardInfo(
            "Bearer $token"
        )
    }

    suspend fun logout(token: String): Response<LogoutResponse> {
        return  api.logout(
            "Bearer $token"
        )
    }
}