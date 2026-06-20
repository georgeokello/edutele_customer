package com.example.edutelecustomer.data.repository


import com.example.edutelecustomer.data.model.apptemplate.ConvertPointsRequest
import com.example.edutelecustomer.data.model.apptemplate.ConvertPointsResponse
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

    suspend fun convertPoints(token: String, points: String): Response<ConvertPointsResponse> {
        return api.convertPoints(
            "Bearer $token",
            ConvertPointsRequest(points.toInt())
        )
    }
}