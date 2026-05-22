package com.example.edutelecustomer.data.repository

import com.example.edutelecustomer.data.model.transactions.TransactionsResponse
import com.example.edutelecustomer.data.remote.ApiService
import retrofit2.Response


class TransactionRepository(private val api: ApiService) {
    suspend fun getCustomerTransactions(token: String): Response<TransactionsResponse> {
        return  api.getTransactions(
            "Bearer $token"
        )

    }
}