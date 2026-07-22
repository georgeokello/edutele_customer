package com.eduteleaccess.customer.data.repository

import com.eduteleaccess.customer.data.model.sendaccess.PhoneLookUpResponse
import com.eduteleaccess.customer.data.model.sendaccess.SendAccessRequest
import com.eduteleaccess.customer.data.model.sendaccess.SendAccessResponse
import com.eduteleaccess.customer.data.remote.ApiService
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