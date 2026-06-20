package com.example.edutelecustomer.data.remote

import com.example.edutelecustomer.data.model.apptemplate.ConvertPointsRequest
import com.example.edutelecustomer.data.model.apptemplate.ConvertPointsResponse
import com.example.edutelecustomer.data.model.apptemplate.LogoutResponse
import com.example.edutelecustomer.data.model.cards.AcceptChildCardResponse
import com.example.edutelecustomer.data.model.cards.CardInfoResponse
import com.example.edutelecustomer.data.model.cards.CardsResponse
import com.example.edutelecustomer.data.model.cards.ChildCardAvailableAccessResponse
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
import com.example.edutelecustomer.data.model.redemptions.RedemptionHistoryResponse
import com.example.edutelecustomer.data.model.sendaccess.PhoneLookUpResponse
import com.example.edutelecustomer.data.model.sendaccess.SendAccessRequest
import com.example.edutelecustomer.data.model.sendaccess.SendAccessResponse
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

    @POST("card-holder/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginDetailsResponse>

    @GET("card-holder/me")
    suspend fun getUserDetails(
        @Header("Authorization") token: String
    ): Response<LoginDetailsResponse>

    @POST("card-holder/logout")
    suspend fun logout(
        @Header("Authorization") token: String,
    ): Response<LogoutResponse>


    // Generate card QR code
    @GET("redemptions/qr/my-card")
    suspend fun generateQrCode(
        @Header("Authorization") token:String
    ): Response<GenerateQrCodeResponse>

    @GET("card-holder/access-events")
    suspend fun getRedemptionsHistory(
        @Header("Authorization") token : String

    ):Response<RedemptionHistoryResponse>

    @GET("card-holder/dashboard")
    suspend fun getCardInfo(
        @Header("Authorization") token : String,

    ):Response<CardInfoResponse>

    // list cards linked to a customer
    @GET("card-holder/family/contacts")
    suspend fun listAvailableCards(
        @Header("Authorization") token : String

    ):Response<CardsResponse>

    // topup child card
    @POST("card-holder/family/{contact_public_id}/allocate")
    suspend fun topUpChild(
        @Header("Authorization") token: String,
        @Path("contact_public_id") contactPublicId: String,
        @Body request: TopUpRequest
    ): Response<TopUpResponse>

    // get child card history
    @GET("card-holder/family/{contact_public_id}/access_events")
    suspend fun getChildHistory(
        @Header("Authorization") token: String,
        @Path("child_public_id") childPublicId: String
    ): Response<ChildCardHistoryResponse>


    @DELETE("card-holder/family/{child_public_id}")
    suspend fun deleteChildCard(
        @Header("Authorization") token: String,
        @Path("child_public_id") childPublicId: String
    ): Response<deleteChildCardResponse>


    // set card spending limit
    @PATCH("card-holder/family/{child_public_id}/spending-limit")
    suspend fun setSpendingLimit(
        @Header("Authorization") token: String,
        @Path("child_public_id") childPublicId: String,
        @Body request: SetSpendingRequest
    ): Response<setSpendingResponse>

    @POST("card-holder/family")
    suspend fun linkChild(
        @Header("Authorization") token : String,
        @Body request: LinkChildRequest

    ):Response<LinkCardResponse>

    @DELETE("card-holder/family/{contact_public_id}")
    suspend fun deleteLinkRequest(
        @Header("Authorization") token: String,
        @Path("contact_public_id") contactPublicId: String
    ): Response<deleteChildCardResponse>

    @POST("card-holder/family/{child_public_id}/freeze")
    suspend fun freezeCard(
        @Header("Authorization") token : String,
        @Path("child_public_id") childPublicId: String,

    ): Response<freezeResponse>

    @POST("card-holder/family/{child_public_id}/unfreeze")
    suspend fun unfreezeCard(
        @Header("Authorization") token : String,
        @Path("child_public_id") childPublicId: String,

    ): Response<unfreezeResponse>


    // get child card balance
    @GET("card-holder/family/{child_public_id}/remaining")
    suspend fun getChildCardAvailableAccess(
        @Header("Authorization") token: String,
        @Path("child_public_id") childPublicId: String
    ): Response<ChildCardAvailableAccessResponse>

    // list card invitations
    @GET("card-holder/family/requests")
    suspend fun ListCardInvitationRequest(
        @Header("Authorization") token: String,
    ): Response<LinkCardInvitationResponse>

    // accept invitation request
    @POST("card-holder/family/requests/{relationship_id}/accept")
    suspend fun acceptInvitation(
        @Header("Authorization") token : String,
        @Path("relationship_id") relationshipId : Int,
        ): Response<AcceptChildCardResponse>

    // decline invitation request
    @POST("card-holder/family/requests/{relationship_id}/decline")
    suspend fun declineInvitation(
        @Header("Authorization") token : String,
        @Path("relationship_id") relationshipId : Int,
    ): Response<declineChildCardResponse>

    // look up phone number
    @GET("card-holder/transfers/lookup")
    suspend fun lookUpPhoneNumber(
        @Header("Authorization") token: String,
        @Query("q") phone: String
    ): Response<PhoneLookUpResponse>

    @POST("card-holder/transfers/send")
    suspend fun sendAccess(
        @Header("Authorization") token: String,
        @Body request: SendAccessRequest
    ): Response<SendAccessResponse>

    @GET("card-holder/family/family-requests")
    suspend fun listJoinFamilyRequest(
        @Header("Authorization") token: String,
    ): Response<FamilyContactResponse>

    // accept invitation request
    @POST("card-holder/family/family-requests/{requester_public_id}/accept")
    suspend fun acceptFamilyInvitation(
        @Header("Authorization") token : String,
        @Path("requester_public_id") requesterPublicId : String,
    ): Response<AcceptChildCardResponse>

    // decline invitation request
    @POST("card-holder/family/family-requests/{requester_public_id}/decline")
    suspend fun declineFamilyInvitation(
        @Header("Authorization") token : String,
        @Path("requester_public_id") requesterPublicId : String,
    ): Response<declineChildCardResponse>

    @POST("/api/rewards/redeem")
    suspend fun convertPoints(
        @Header("Authorization") token : String,
        @Body request: ConvertPointsRequest
    ): Response<ConvertPointsResponse>

}