package com.example.edutelecustomer.ui.screens.cards

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edutelecustomer.data.local.UserPreferences
import com.example.edutelecustomer.data.model.cards.LinkCardInvitationItems
import com.example.edutelecustomer.data.model.cards.LinkedCardItem
import com.example.edutelecustomer.data.model.transactions.TransactionItem
import com.example.edutelecustomer.data.repository.CardsRepository
import com.example.edutelecustomer.ui.screens.historyscreen.TransactionUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// 📊 UI State
data class CardsUiState(
    val isLoading: Boolean = false,
    val cards: List<LinkedCardItem> = emptyList(),
    val message: String = "",
    val error: String? = null
)

data class CardInvitationUiState(
    val isLoading: Boolean = false,
    val invitations : List<LinkCardInvitationItems> = emptyList(),
    val message: String = "",
    val total: Int = 0,
    val error: String? = null
)

data class ChildCardBalanceUiState(
    val child_public_id: String ="",
    val full_name: String ="",
    val card_number: String ="",
    val balance: String ="",
    val has_card: Boolean = true
)


data class LinkFamilyMemberUiState(
    val showDialog: Boolean = false,

    val phoneNumber: String = "",
    val relationship: String = "",

    val allowTopUp: Boolean = true,
    val viewBalance: Boolean = true,
    val viewHistory: Boolean = true,
    val freezeCard: Boolean = false,

    val relationshipOptions: List<String> = listOf(
        "Parent",
        "Guardian",
        "Child",
        "Spouse",
        "Sibling",
        "Relative",
        "Employer",
        "Employee",
        "Caregiver",
        "Friend",
        "Others"
    )
)

class CardsViewModel(
    private val userPreferences: UserPreferences,
    private val repository: CardsRepository

): ViewModel() {

    private val _uiState = MutableStateFlow(CardsUiState())
    val uiState: StateFlow<CardsUiState> = _uiState

    private  val _invitationUiState = MutableStateFlow(CardInvitationUiState())
    val invitationUiState: StateFlow<CardInvitationUiState> = _invitationUiState

    var showSendDialog = MutableStateFlow(false)
        private set

    var showSendConfirmDialog = MutableStateFlow(false)
        private set


    var showSetLimitDialog = MutableStateFlow(false)
        private set

    var showDeleteCardDialog = MutableStateFlow(false)
        private set

    var setSuccessDialog = MutableStateFlow(false)
        private set

    var showChildCardBalance = MutableStateFlow(false)
        private set


    private val _uiLinkState = MutableStateFlow(LinkFamilyMemberUiState())
    val uiLinkState = _uiLinkState.asStateFlow()

    private val _uiChildBalanceState = MutableStateFlow(ChildCardBalanceUiState())
    val uiChildBalanceState = _uiChildBalanceState.asStateFlow()


    val uiChildCardState = _uiChildBalanceState.asStateFlow()

    fun openLinkCardDialog() {
        _uiLinkState.update { it.copy(showDialog = true) }
    }

    fun closeLinkCardDialog() {
        _uiLinkState.update { it.copy(showDialog = false) }
    }

    fun updatePhone(value: String) {
        _uiLinkState.update { it.copy(phoneNumber = value) }
    }

    fun updateRelationship(value: String) {
        _uiLinkState.update { it.copy(relationship = value) }
    }

    fun updateAllowTopUp(value: Boolean) {
        _uiLinkState.update { it.copy(allowTopUp = value) }
    }

    fun updateViewBalance(value: Boolean) {
        _uiLinkState.update { it.copy(viewBalance = value) }
    }

    fun updateViewHistory(value: Boolean) {
        _uiLinkState.update { it.copy(viewHistory = value) }
    }

    fun updateFreezeCard(value: Boolean) {
        _uiLinkState.update { it.copy(freezeCard = value) }
    }



    val username: StateFlow<String?> =
        userPreferences.usernameFlow
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )

    fun init() {
        if (_invitationUiState.value.invitations.isEmpty()) {
            fetchInvitation()
            fetchCards()
        }
    }

    fun fetchCards() {
        viewModelScope.launch {

            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            try {
                val tokenValue =
                    userPreferences.tokenFlow.first()

                if(!tokenValue.isNullOrEmpty()){

                    val response = repository.listAvailableCards(tokenValue)

                    if (response.isSuccessful) {

                        val cards =
                            response.body()?.items ?: emptyList()

                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            cards = cards,
                            message =  ""
                        )
                        if(cards.isEmpty()){
                            _uiState.value = _uiState.value.copy(
                                message = "You Have No Cards Under this Account"
                            )
                        }

                    } else {

                        val errorBody =
                            response.errorBody()?.string()
                    }

                }else{
                    _uiState.value = _uiState.value.copy(
                        error = "No token found"
                    )
                }


            } catch (e: Exception) {

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to fetch"
                )
            }
        }
    }

    fun fetchInvitation(){
        viewModelScope.launch {
            _invitationUiState.value = _invitationUiState.value.copy(
                isLoading = true,
                error = null
            )
            try {
                val tokenValue = userPreferences.tokenFlow.first()
                if(!tokenValue.isNullOrEmpty()){
                    val response = repository.ListCardInvitationRequest(tokenValue)
                    if(response.isSuccessful){
                        val invitation = response.body()?.items ?: emptyList()
                        val total = response.body()?.total ?: 0
                        _invitationUiState.value = _invitationUiState.value.copy(
                            isLoading = false,
                            invitations =  invitation,
                            total = total,
                        )
                        if(invitation.isEmpty()){
                            _invitationUiState.value = _invitationUiState.value.copy(
                                message = "No Invitation Available"
                            )
                        }
                    }
                }else{
                    _invitationUiState.value = _invitationUiState.value.copy(
                        isLoading = false,
                        error = "No Token Found"
                    )
                }
            }catch (e:Exception){
                _invitationUiState.value = _invitationUiState.value.copy(
                    isLoading = false,
                    error = "Failed to fetch invitations"
                )
            }
        }
    }

    fun AcceptInvitation(relationshipId: Int){
        viewModelScope.launch {
            try {
                val tokenValue = userPreferences.tokenFlow.first()
                if(!tokenValue.isNullOrEmpty()){
                    val response = repository.AcceptInvitation(
                        tokenValue, relationshipId
                    )
                    setSuccessDialog.value = true
                    _invitationUiState.value = _invitationUiState.value.copy(
                        invitations = emptyList(),
                        total = 0
                    )
                }
            }catch (e: Exception){
                _uiState.value = _uiState.value.copy(
                    error = "Failed to accept card"
                )
            }
        }
    }

    fun DeclineInvitation(relationshipId: Int){
        viewModelScope.launch {
            try {
                val tokenValue = userPreferences.tokenFlow.first()
                if(!tokenValue.isNullOrEmpty()){
                    val response = repository.DeclineInvitation(
                        tokenValue, relationshipId
                    )

                    setSuccessDialog.value = true
                    _invitationUiState.value = _invitationUiState.value.copy(
                        invitations = emptyList(),
                        total = 0

                    )

                }
            }catch (e: Exception){

                Log.e("DECLINE_ERROR", "Error", e)

                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Unknown error"
                )
            }
        }
    }

    fun SendMoney(childPublicId: String, amount: String, pin: String, remarks: String){
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )
            try {
                val tokenValue = userPreferences.tokenFlow.first()

                if(!tokenValue.isNullOrEmpty()){
                    val response = repository.topUpChildCard(tokenValue,childPublicId,amount,pin,remarks)
                    if(response.isSuccessful){
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = null
                        )
                        showSendConfirmDialog.value = false
                        setSuccessDialog.value = true

                    }
                }else{
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "No token found"
                    )

                }

            }catch (e: Exception){
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to Send Money"
                )
            }
        }

    }

    fun deleteCard(card_public_id: String){

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
            )
            try {
                val tokenValue = userPreferences.tokenFlow.first()
                if(!tokenValue.isNullOrEmpty()){
                    val response = repository.deteleChildCard(tokenValue, card_public_id)
                    if(response.isEmpty()){
                        setSuccessDialog.value = true
                    }

                }
            }catch (e:Exception){
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to delete card"
                )
            }
        }

    }

    fun setCardLimit(card_public_id: String, limitAmount: String){
        viewModelScope.launch {
            setSuccessDialog.value = false
            _uiState.value = _uiState.value.copy(
                isLoading = true,
            )
            try {
                val tokenValue = userPreferences.tokenFlow.first()
                if(!tokenValue.isNullOrEmpty()){
                    val response = repository.setCardLimit(tokenValue, card_public_id, limitAmount)
                    if(response.isSuccessful){
                        showSetLimitDialog.value = false
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                        )
                        setSuccessDialog.value = true
                    }
                }
            }catch (e:Exception){
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to Set Limit"
                )
            }
        }
    }

    fun LinkChildCard(phoneNumber: String, relationship: String, allowTopUp: Boolean,
                      viewBalance: Boolean,
                      viewHistory: Boolean,
                      freezeCard: Boolean){
        viewModelScope.launch {
            try{
                val tokenValue = userPreferences.tokenFlow.first()
                if(!tokenValue.isNullOrEmpty()){
                    val response = repository.linkCard(tokenValue, phoneNumber, relationship, allowTopUp, viewBalance, viewHistory, freezeCard )
                    if(response.isSuccessful){
                        setSuccessDialog.value = true
                    }
                }
            }catch (e:Exception){
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to Send Link Card request"
                )
            }
        }
    }

    fun freezeCard(child_public_id: String){
        viewModelScope.launch {
            try {
                val tokenValue = userPreferences.tokenFlow.first()
                if(!tokenValue.isNullOrEmpty()){
                    val response = repository.freezeCard(tokenValue, child_public_id)
                    if(response.isNotEmpty()){
                        setSuccessDialog.value = true
                    }
                }
            }catch (e:Exception){
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to Freeze Card"
                )
            }
        }
    }

    fun unFreezeCard(child_public_id: String){
        viewModelScope.launch {
            try {
                val tokenValue = userPreferences.tokenFlow.first()
                if(!tokenValue.isNullOrEmpty()){
                    val response = repository.unfreezeCard(tokenValue, child_public_id)
                    if(response.isNotEmpty()){
                        setSuccessDialog.value = true
                    }
                }
            }catch (e:Exception){
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to Freeze Card"
                )
            }
        }
    }


    fun getChildCardBalance(child_public_id: String){
        viewModelScope.launch {
            try {
                val tokenValue = userPreferences.tokenFlow.first()
                if(!tokenValue.isNullOrEmpty()){
                    val response = repository.getChildCardBalance(tokenValue, child_public_id)
                    if(response.isSuccessful){
                        _uiChildBalanceState.value = _uiChildBalanceState.value.copy(
                            balance =  response.body()!!.balance
                        )
                    }
                }
            }catch (e:Exception){
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to get Card Balance"
                )
            }
        }
    }



    fun openSendDialog(){
        showSendDialog.value = true
    }

    fun closeSendDialog(){
        showSendDialog.value = false
    }

    fun openSendConfirmDialog(){
        showSendConfirmDialog.value = true
    }

    fun closeSendConfirmDialog(){
        showSendConfirmDialog.value = false
    }

    fun openDeleteCardDialog(){
        showDeleteCardDialog.value = true
    }

    fun closeDeleteCardDialog(){
        showDeleteCardDialog.value = false
    }

    fun openSetLimitDialog(){
        showSetLimitDialog.value = true
    }

    fun closeSetLimitDialog(){
        showSetLimitDialog.value = false
    }

    fun openSuccessDialog(){
        setSuccessDialog.value = true
    }

    fun closeSuccessDialog(){
        setSuccessDialog.value = false
    }

    fun showChildCardBalance(){
        showChildCardBalance.value = true
    }

    fun hideChildCardBalance(){
        showChildCardBalance.value = false
    }

}