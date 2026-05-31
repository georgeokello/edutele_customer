package com.example.edutelecustomer.data.repository

import com.example.edutelecustomer.data.model.cards.AcceptChildCardResponse
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
import com.example.edutelecustomer.data.remote.ApiService
import retrofit2.Response

class CardsRepository( private val api: ApiService) {
    suspend fun listAvailableCards(token: String): Response<CardsResponse>{
        return api.listAvailableCards(
            "Bearer $token"
        )
    }

    suspend fun topUpChildCard(token: String, path: String, amount: String, pin: String, remarks: String): Response<TopUpResponse>{
        return  api.topUpChild(
            "Bearer $token",
            path,
            TopUpRequest(
                amount.toInt(), pin, remarks
            )
        )
    }

    suspend fun getChildCardHistory(token: String, path: String): Response<ChildCardHistoryResponse>{
        return api.getChildHistory(
            "Bearer $token",
            path,
        )
    }

    suspend fun deteleChildCard(token: String, path: String): Response<deleteChildCardResponse> {
        return api.deleteChildCard(
            "Bearer $token",
            path
        )
    }

    suspend fun setCardLimit(token: String, path: String, limitAmount: String): Response<setSpendingResponse>{
        return api.setSpendingLimit(
            "Bearer  $token",
            path,
            SetSpendingRequest(
                limitAmount.toInt()
            )
        )
    }

    suspend fun linkCard(token: String, childPhone: String, relationshipType: String,canTopup: Boolean, canViewBalance: Boolean, canViewHistory: Boolean, canFreeze: Boolean): Response<LinkCardResponse>{
        return  api.linkChild(
            "Bearer $token",
            LinkChildRequest(
                childPhone,
                relationshipType,
                canTopup,
                canViewBalance,
                canViewHistory,
                canFreeze
            )
        )
    }

    suspend fun freezeCard(token: String, path: String): Response<freezeResponse> {
        return api.freezeCard(
            "Bearer $token",
            path
        )
    }

    suspend fun unfreezeCard(token: String, path:String): Response<unfreezeResponse> {
        return api.unfreezeCard(
            "Bearer $token",
            path
        )
    }

    suspend fun getChildCardBalance(token: String, path:String): Response<ChildCardBalanceResponse>{
        return api.getChildCardBalance(
            "Bearer $token",
            path
        )
    }

    suspend fun ListCardInvitationRequest(token: String): Response<LinkCardInvitationResponse>{
        return  api.ListCardInvitationRequest(
            "Bearer $token"
        )
    }

    suspend fun AcceptInvitation(token: String, path: Int): Response<AcceptChildCardResponse> {
        return api.acceptInvitation(
            "Bearer ${token}",
            path
        )
    }

    suspend fun DeclineInvitation(token: String, path: Int): Response<declineChildCardResponse> {
        return api.declineInvitation(
            "Bearer ${token}",
            path
        )
    }

    suspend fun listJoinFamilyRequest(token: String): Response<FamilyContactResponse>{
        return api.listJoinFamilyRequest(
            "Bearer $token"
        )
    }

    suspend fun AcceptFamilyInvitation(token: String, path: String): Response<AcceptChildCardResponse> {
        return api.acceptFamilyInvitation(
            "Bearer ${token}",
            path
        )
    }

    suspend fun DeclineFamilyInvitation(token: String, path: String): Response<declineChildCardResponse> {
        return api.declineFamilyInvitation(
            "Bearer ${token}",
            path
        )
    }
}
