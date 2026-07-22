package com.eduteleaccess.customer.data.repository

import com.eduteleaccess.customer.data.model.redemptions.RedemptionHistoryResponse
import com.eduteleaccess.customer.data.remote.ApiService
import retrofit2.Response

class RedemptionHistoryRepository(private val api: ApiService) {
    suspend fun getCustomerRedemptions(token: String): Response<RedemptionHistoryResponse> {
        return  api.getRedemptionsHistory(
            "Bearer $token"
        )

    }
}

