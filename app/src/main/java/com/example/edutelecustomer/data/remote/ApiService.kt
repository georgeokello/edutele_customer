package com.example.edutelecustomer.data.remote

import com.example.edutelecustomer.data.model.cards.AcceptChildCardResponse
import com.example.edutelecustomer.data.model.cards.CardInfoResponse
import com.example.edutelecustomer.data.model.cards.CardsResponse
import com.example.edutelecustomer.data.model.cards.ChildCardBalanceResponse
import com.example.edutelecustomer.data.model.cards.ChildCardHistoryResponse
import com.example.edutelecustomer.data.model.cards.FamilyContactResponse
import com.example.edutelecustomer.data.model.cards.LinkCardInvitationResponse
import com.example.edutelecustomer.data.model.cards.LinkCardResponse
import com.example.edutelecustomer.data.model.cards.LinkChildRequest
import com.example.edutelecustomer.data.model.cards.SetSpendingRequest
import com.example.edutelecustomer.data.model.cards.TopUpRequest
import com.example.edutelecustomer.data.model.cards.TopUpResponse
import com.example.edutelecustomer.data.model.cards.declineChildCardResponse
import com.example.edutelecustomer.data.model.cards.deleteChildCardResponse
import com.example.edutelecustomer.data.model.cards.freezeResponse
import com.example.edutelecustomer.data.model.cards.setSpendingResponse
import com.example.edutelecustomer.data.model.cards.unfreezeResponse
import com.example.edutelecustomer.data.model.login.LoginDetailsResponse
import com.example.edutelecustomer.data.model.login.LoginRequest
import com.example.edutelecustomer.data.model.qrcode.GenerateQrCodeResponse
import com.example.edutelecustomer.data.model.sendmoney.PhoneLookUpRequest
import com.example.edutelecustomer.data.model.sendmoney.PhoneLookUpResponse
import com.example.edutelecustomer.data.model.sendmoney.SendMoneyRequest
import com.example.edutelecustomer.data.model.sendmoney.SendMoneyResponse
import com.example.edutelecustomer.data.model.transactions.TransactionsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

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
    @GET("customer/family/contacts")
    suspend fun listAvailableCards(
        @Header("Authorization") token : String

    ):Response<CardsResponse>

    // topup child card
    @POST("customer/family/{contact_public_id}/topup")
    suspend fun topUpChild(
        @Header("Authorization") token: String,
        @Path("contact_public_id") contactPublicId: String,
        @Body request: TopUpRequest
    ): Response<TopUpResponse>

    // get child card history
    @GET("customer/family/{child_public_id}/transactions")
    suspend fun getChildHistory(
        @Header("Authorization") token: String,
        @Path("child_public_id") childPublicId: String
    ): Response<ChildCardHistoryResponse>


    @DELETE("/api/customer/family/{child_public_id}")
    suspend fun deleteChildCard(
        @Header("Authorization") token: String,
        @Path("child_public_id") childPublicId: String
    ): Response<deleteChildCardResponse>


    // set card spending limit
    @PATCH("customer/family/{child_public_id}/spending-limit")
    suspend fun setSpendingLimit(
        @Header("Authorization") token: String,
        @Path("child_public_id") childPublicId: String,
        @Body request: SetSpendingRequest
    ): Response<setSpendingResponse>

    @POST("customer/family")
    suspend fun linkChild(
        @Header("Authorization") token : String,
        @Body request: LinkChildRequest

    ):Response<LinkCardResponse>

    @POST("customer/family/{child_public_id}/freeze")
    suspend fun freezeCard(
        @Header("Authorization") token : String,
        @Path("child_public_id") childPublicId: String,

    ): Response<freezeResponse>

    @POST("customer/family/{child_public_id}/unfreeze")
    suspend fun unfreezeCard(
        @Header("Authorization") token : String,
        @Path("child_public_id") childPublicId: String,

    ): Response<unfreezeResponse>


    // get child card balance
    @GET("customer/family/{child_public_id}/balance")
    suspend fun getChildCardBalance(
        @Header("Authorization") token: String,
        @Path("child_public_id") childPublicId: String
    ): Response<ChildCardBalanceResponse>

    // list card invitations
    @GET("customer/family/requests")
    suspend fun ListCardInvitationRequest(
        @Header("Authorization") token: String,
    ): Response<LinkCardInvitationResponse>

    // accept invitation request
    @POST("customer/family/requests/{relationship_id}/accept")
    suspend fun acceptInvitation(
        @Header("Authorization") token : String,
        @Path("relationship_id") relationshipId : Int,
        ): Response<AcceptChildCardResponse>

    // decline invitation request
    @POST("customer/family/requests/{relationship_id}/decline")
    suspend fun declineInvitation(
        @Header("Authorization") token : String,
        @Path("relationship_id") relationshipId : Int,
    ): Response<declineChildCardResponse>

    // look up phone number
    @GET("customer/transfers/lookup")
    suspend fun lookUpPhoneNumber(
        @Header("Authorization") token: String,
        @Query("phone") phone: String
    ): Response<PhoneLookUpResponse>

    @POST("customer/transfers/send")
    suspend fun SendMoney(
        @Header("Authorization") token: String,
        @Body request: SendMoneyRequest
    ): Response<SendMoneyResponse>

    @GET("customer/family/family-requests")
    suspend fun listJoinFamilyRequest(
        @Header("Authorization") token: String,
    ): Response<FamilyContactResponse>

    // accept invitation request
    @POST("/api/customer/family/family-requests/{requester_public_id}/accept")
    suspend fun acceptFamilyInvitation(
        @Header("Authorization") token : String,
        @Path("requester_public_id") requesterPublicId : String,
    ): Response<AcceptChildCardResponse>

    // decline invitation request
    @POST("/api/customer/family/family-requests/{requester_public_id}/decline")
    suspend fun declineFamilyInvitation(
        @Header("Authorization") token : String,
        @Path("requester_public_id") requesterPublicId : String,
    ): Response<declineChildCardResponse>

}