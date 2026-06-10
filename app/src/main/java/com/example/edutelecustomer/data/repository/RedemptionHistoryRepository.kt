package com.example.edutelecustomer.data.repository

import com.example.edutelecustomer.data.model.redemptions.RedemptionHistoryResponse
import com.example.edutelecustomer.data.remote.ApiService
import retrofit2.Response

class RedemptionHistoryRepository(private val api: ApiService) {
    suspend fun getCustomerRedemptions(token: String): Response<RedemptionHistoryResponse> {
        return  api.getRedemptionsHistory(
            "Bearer $token"
        )

    }
}

