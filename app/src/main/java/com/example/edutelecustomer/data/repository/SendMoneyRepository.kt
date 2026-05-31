package com.example.edutelecustomer.data.repository

import com.example.edutelecustomer.data.model.sendmoney.PhoneLookUpRequest
import com.example.edutelecustomer.data.model.sendmoney.PhoneLookUpResponse
import com.example.edutelecustomer.data.model.sendmoney.SendMoneyRequest
import com.example.edutelecustomer.data.model.sendmoney.SendMoneyResponse
import com.example.edutelecustomer.data.remote.ApiService
import retrofit2.Response

class SendMoneyRepository(private val api: ApiService){

    suspend fun lookUpPhoneNumber(token: String, phone: String) : Response<PhoneLookUpResponse>{
        return api.lookUpPhoneNumber(
            "Bearer $token",
            phone
        )
    }

    suspend fun sendMoney(token: String, publicId: String, amount: String, remarks: String, pin:String): Response<SendMoneyResponse>{
        return api.SendMoney(
            "Bearer $token",
            SendMoneyRequest(
                publicId, amount.toInt(),pin,remarks
            )
        )
    }

}