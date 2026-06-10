package com.example.edutelecustomer.data.repository

import com.example.edutelecustomer.data.model.sendaccess.PhoneLookUpResponse
import com.example.edutelecustomer.data.model.sendaccess.SendAccessRequest
import com.example.edutelecustomer.data.model.sendaccess.SendAccessResponse
import com.example.edutelecustomer.data.remote.ApiService
import retrofit2.Response

class SendAccessRepository(private val api: ApiService){

    suspend fun lookUpPhoneNumber(token: String, phone: String) : Response<PhoneLookUpResponse>{
        return api.lookUpPhoneNumber(
            "Bearer $token",
            phone
        )
    }

    suspend fun sendAccess(token: String, publicId: String, accessValue: String, remarks: String, pin:String): Response<SendAccessResponse>{
        return api.sendAccess(
            "Bearer $token",
            SendAccessRequest(
                publicId, accessValue.toInt(),pin,remarks
            )
        )
    }

}