package com.eduteleaccess.customer.data.repository



import com.eduteleaccess.customer.data.model.qrcode.GenerateQrCodeResponse
import com.eduteleaccess.customer.data.remote.ApiService
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
