package com.example.edutelecustomer.data.repository



import com.example.edutelecustomer.data.model.qrcode.GenerateQrCodeResponse
import com.example.edutelecustomer.data.remote.ApiService
import retrofit2.Response

class QrCodeRespository(
    private val api: ApiService
) {

    suspend fun generateQrCode(token: String): Response<GenerateQrCodeResponse> {
        return api.generateQrCode(
            "Bearer $token"
        )
    }
}
