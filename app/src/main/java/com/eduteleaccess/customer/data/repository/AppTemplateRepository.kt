package com.eduteleaccess.customer.data.repository


import com.eduteleaccess.customer.data.model.apptemplate.ConvertPointsRequest
import com.eduteleaccess.customer.data.model.apptemplate.ConvertPointsResponse
import com.eduteleaccess.customer.data.model.apptemplate.LogoutResponse
import com.eduteleaccess.customer.data.model.cards.CardInfoResponse
import com.eduteleaccess.customer.data.remote.ApiService
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