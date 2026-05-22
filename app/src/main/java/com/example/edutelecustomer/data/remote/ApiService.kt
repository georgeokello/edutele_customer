package com.example.edutelecustomer.data.remote

import com.example.edutelecustomer.data.model.apptemplate.BalanceResponse
import com.example.edutelecustomer.data.model.cards.AcceptResponse
import com.example.edutelecustomer.data.model.cards.CardInfoResponse
import com.example.edutelecustomer.data.model.cards.CardsResponse
import com.example.edutelecustomer.data.model.cards.ChildCardBalanceResponse
import com.example.edutelecustomer.data.model.cards.ChildCardHistoryRequest
import com.example.edutelecustomer.data.model.cards.ChildCardHistoryResponse
import com.example.edutelecustomer.data.model.cards.DeclineResponse
import com.example.edutelecustomer.data.model.cards.LinkCardInvitationResponse
import com.example.edutelecustomer.data.model.cards.LinkCardResponse
import com.example.edutelecustomer.data.model.cards.LinkChildRequest
import com.example.edutelecustomer.data.model.cards.SetSpendingRequest
import com.example.edutelecustomer.data.model.cards.TopUpRequest
import com.example.edutelecustomer.data.model.cards.TopUpResponse
import com.example.edutelecustomer.data.model.cards.setSpendingResponse
import com.example.edutelecustomer.data.model.login.LoginDetailsResponse
import com.example.edutelecustomer.data.model.login.LoginRequest
import com.example.edutelecustomer.data.model.qrcode.GenerateQrCodeResponse
import com.example.edutelecustomer.data.model.transactions.TransactionsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.Path

interface ApiService {

    @POST("customer/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginDetailsResponse>

    @GET("customer/me")
    suspend fun getUserDetails(
        @Header("Authorization") token: String
    ): Response<LoginDetailsResponse>

    @GET("payments/qr/my-card")
    suspend fun generateQrCode(
        @Header("Authorization") token:String
    ): Response<GenerateQrCodeResponse>

    @GET("customer/transactions")
    suspend fun getTransactions(
        @Header("Authorization") token : String

    ):Response<TransactionsResponse>

    @GET("customer/dashboard")
    suspend fun getCardInfo(
        @Header("Authorization") token : String,

    ):Response<CardInfoResponse>

    // list cards linked to a customer
    @GET("customer/relationships")
    suspend fun listAvailableCards(
        @Header("Authorization") token : String

    ):Response<CardsResponse>

    // topup child card
    @POST("/api/customer/relationships/{child_public_id}/topup")
    suspend fun topUpChild(
        @Header("Authorization") token: String,
        @Path("child_public_id") childPublicId: String,
        @Body request: TopUpRequest
    ): Response<TopUpResponse>

    // get child card history
    @GET("customer/relationships/{child_public_id}/transactions")
    suspend fun getChildHistory(
        @Header("Authorization") token: String,
        @Path("child_public_id") childPublicId: String
    ): Response<ChildCardHistoryResponse>


    @DELETE("/api/customer/relationships/{child_public_id}")
    suspend fun deleteChildCard(
        @Header("Authorization") token: String,
        @Path("child_public_id") childPublicId: String
    ): String

    // set card spending limit
    @PATCH("customer/relationships/{child_public_id}/spending-limit")
    suspend fun setSpendingLimit(
        @Header("Authorization") token: String,
        @Path("child_public_id") childPublicId: String,
        @Body request: SetSpendingRequest
    ): Response<setSpendingResponse>

    @POST("customer/relationships")
    suspend fun linkChild(
        @Header("Authorization") token : String,
        @Body request: LinkChildRequest

    ):Response<LinkCardResponse>

    @POST("customer/relationships/{child_public_id}/freeze")
    suspend fun freezeCard(
        @Header("Authorization") token : String,
        @Path("child_public_id") childPublicId: String,

    ): String

    @POST("customer/relationships/{child_public_id}/unfreeze")
    suspend fun unfreezeCard(
        @Header("Authorization") token : String,
        @Path("child_public_id") childPublicId: String,

    ): String


    // get child card balance
    @GET("customer/relationships/{child_public_id}/balance")
    suspend fun getChildCardBalance(
        @Header("Authorization") token: String,
        @Path("child_public_id") childPublicId: String
    ): Response<ChildCardBalanceResponse>

    // list card invitations
    @GET("customer/relationships/requests")
    suspend fun ListCardInvitationRequest(
        @Header("Authorization") token: String,
    ): Response<LinkCardInvitationResponse>

    // accept invitation request
    @POST("customer/relationships/requests/{relationship_id}/accept")
    suspend fun acceptInvitation(
        @Header("Authorization") token : String,
        @Path("relationship_id") relationshipId : Int,
        ): AcceptResponse

    // decline invitation request
    @POST("customer/relationships/requests/{relationship_id}/decline")
    suspend fun declineInvitation(
        @Header("Authorization") token : String,
        @Path("relationship_id") relationshipId : Int,
    ): DeclineResponse

}