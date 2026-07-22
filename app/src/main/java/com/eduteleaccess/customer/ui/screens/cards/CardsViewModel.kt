package com.eduteleaccess.customer.ui.screens.cards

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eduteleaccess.customer.data.local.UserPreferences
import com.eduteleaccess.customer.data.model.cards.FamilyContactItems
import com.eduteleaccess.customer.data.model.cards.LinkCardInvitationItems
import com.eduteleaccess.customer.data.model.cards.RelationshipItem
import com.eduteleaccess.customer.data.repository.CardsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONObject

// 📊 UI State
data class CardsUiState(
    val isLoading: Boolean = false,
    val cards: List<RelationshipItem> = emptyList(),
    val message: String = "",
    val error: String? = null
)

data class JoinFamilyUiState(
    val isLoading: Boolean = false,
    val total: Int = 0,
    val familyRequest: List<FamilyContactItems> = emptyList(),
    val error: String? = null
)

data class CardInvitationUiState(
    val isLoading: Boolean = false,
    val invitations : List<LinkCardInvitationItems> = emptyList(),
    val message: String = "",
    val total: Int = 0,
    val error: String? = null
)

data class ChildCardAvailableAccessUiState(
    val child_public_id: String ="",
    val full_name: String ="",
    val card_number: String ="",
    val accessValue: String ="",
    val has_card: Boolean = true
)


data class LinkFamilyMemberUiState(
    val showDialog: Boolean = false,

    val childPublicId: String = "",
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

    private  val _joinInvitationUiState = MutableStateFlow(JoinFamilyUiState())
    val joinInvitationUiState: StateFlow<JoinFamilyUiState> = _joinInvitationUiState

    var showSendDialog = MutableStateFlow(false)
        private set

    var showSendConfirmDialog = MutableStateFlow(false)
        private set

    var dialogMessage = MutableStateFlow("")
        private set


    var showSetLimitDialog = MutableStateFlow(false)
        private set

    var showDeleteCardDialog = MutableStateFlow(false)
        private set

    var setSuccessDialog = MutableStateFlow(false)
        private set

    var setFailureDialog = MutableStateFlow(false)
        private set

    var showChildCardAvailableAccess = MutableStateFlow(false)
        private set

    var selectedCard = MutableStateFlow<RelationshipItem?>(null)
        private set


    private val _uiLinkState = MutableStateFlow(LinkFamilyMemberUiState())
    val uiLinkState = _uiLinkState.asStateFlow()

    private val _uiChildAvailableAccessState = MutableStateFlow(ChildCardAvailableAccessUiState())
    val uiChildAvailableAccessState = _uiChildAvailableAccessState.asStateFlow()


    val uiChildCardState = _uiChildAvailableAccessState.asStateFlow()

    fun openLinkCardDialog(card: RelationshipItem) {
        selectedCard.value = card
        _uiLinkState.update { it.copy(showDialog = true) }
    }

    fun closeLinkCardDialog() {
        selectedCard.value = null
        _uiLinkState.update { it.copy(showDialog = false) }
    }

    fun updateChildPublicId(value: String) {
        _uiLinkState.update { it.copy(childPublicId = value) }
    }

    fun updateRelationship(value: String) {
        _uiLinkState.update { it.copy(relationship = value) }
    }

    fun updateAllowTopUp(value: Boolean) {
        _uiLinkState.update { it.copy(allowTopUp = value) }
    }

    fun updateViewAvailableAccess(value: Boolean) {
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
            listJoinFamilyRequest()
            fetchCards()
        }
    }

    private fun fetchCards() {
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

                        Log.d("Fetch cards", "${response.body()?.items ?: emptyList()}")

                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            cards = cards,
                            message =  ""
                        )
                        if(cards.isEmpty()){
                            _uiState.value = _uiState.value.copy(
                                message = "You Have No Cards Under this Account",
                                isLoading = false

                            )
                        }

                    } else {
                        val errorBody = response.errorBody()?.string()

                        val errorDetail = errorBody?.let {
                            try {
                                JSONObject(it).getString("detail")
                            } catch (e: Exception) {
                                "Unknown error, Try again Later"
                            }
                        }

                        _uiState.value = _uiState.value.copy(
                            error = errorDetail,
                            isLoading = false

                        )

                    }

                }else{
                    _uiState.value = _uiState.value.copy(
                        error = "No token found",
                        isLoading = false

                    )
                }


            } catch (e: Exception) {

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Something went wrong, Try again Later"
                )
            }
        }
    }

    private fun fetchInvitation(){
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
                                message = "No Invitation Available",
                                isLoading = false
                            )
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()

                        val errorDetail = errorBody?.let {
                            try {
                                JSONObject(it).getString("detail")
                            } catch (e: Exception) {
                                "Unknown error, Try again Later"
                            }
                        }

                        _invitationUiState.value = _invitationUiState.value.copy(
                            error = errorDetail,
                            isLoading = false

                        )

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
                    error = "Something went wrong, Try again Later"
                )

            }
        }
    }

    fun acceptInvitation(relationshipId: Int){
        viewModelScope.launch {
            try {
                val tokenValue = userPreferences.tokenFlow.first()
                if(!tokenValue.isNullOrEmpty()){
                    val response = repository.AcceptInvitation(
                        tokenValue, relationshipId
                    )
                    if(response.isSuccessful){
                        dialogMessage.value = "Invitation Accepted Successfully"
                        setSuccessDialog.value = true
                        _invitationUiState.value = _invitationUiState.value.copy(
                            invitations = emptyList(),
                            total = 0
                        )
                        fetchInvitation()
                        fetchCards()
                    }else{
                        val errorBody = response.errorBody()?.string()

                        val errorDetail = errorBody?.let {
                            try {
                                JSONObject(it).getString("detail")
                            } catch (e: Exception) {
                                "Unknown error, Try again Later"
                            }
                        }

                        _invitationUiState.value = _invitationUiState.value.copy(
                            error = errorDetail,
                            isLoading = false

                        )
                        if (errorDetail != null) {
                            dialogMessage.value = errorDetail
                        }
                        setFailureDialog.value = true

                        fetchInvitation()
                        fetchCards()
                        listJoinFamilyRequest()
                    }

                }
            }catch (e: Exception){
                _invitationUiState.value = _invitationUiState.value.copy(
                    error = "Something went wrong, Try again Later"
                )
            }
        }
    }

    fun declineInvitation(relationshipId: Int){
        viewModelScope.launch {
            try {
                val tokenValue = userPreferences.tokenFlow.first()
                if(!tokenValue.isNullOrEmpty()){
                    val response = repository.DeclineInvitation(
                        tokenValue, relationshipId
                    )
                    if(response.isSuccessful){
                        dialogMessage.value = "Invitation Declined Successfully"
                        setSuccessDialog.value = true
                        _invitationUiState.value = _invitationUiState.value.copy(
                            invitations = emptyList(),
                            total = 0

                        )
                        fetchInvitation()
                        fetchCards()
                    }else{
                        val errorBody = response.errorBody()?.string()

                        val errorDetail = errorBody?.let {
                            try {
                                JSONObject(it).getString("detail")
                            } catch (e: Exception) {
                                "Unknown error, Try again Later"
                            }
                        }

                        _invitationUiState.value = _invitationUiState.value.copy(
                            error = errorDetail,
                            isLoading = false

                        )
                        if (errorDetail != null) {
                            dialogMessage.value = errorDetail
                        }
                        setFailureDialog.value = true
                        fetchInvitation()
                        fetchCards()
                        listJoinFamilyRequest()
                    }

                }
            }catch (e: Exception){

                Log.e("DECLINE_ERROR", "Error", e)

                _invitationUiState.value = _invitationUiState.value.copy(
                    error = "Something went wrong, Try again Later"
                )

            }
        }
    }

    fun sendAccess(childPublicId: String, accessValue: String, pin: String, remarks: String){
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )
            try {
                val tokenValue = userPreferences.tokenFlow.first()

                if(!tokenValue.isNullOrEmpty()){
                    val response = repository.topUpChildCard(tokenValue,childPublicId, accessValue,pin,remarks)
                    if(response.isSuccessful){
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = null
                        )
                        showSendConfirmDialog.value = false
                        dialogMessage.value = "Access Sent Successfully"
                        setSuccessDialog.value = true

                    } else {
                        val errorBody = response.errorBody()?.string()

                        val errorDetail = errorBody?.let {
                            try {
                                JSONObject(it).getString("detail")
                            } catch (e: Exception) {
                                "Unknown error, Try again Later"
                            }
                        }

                        _uiState.value = _uiState.value.copy(
                            error = errorDetail,
                            isLoading = false

                        )
                        if (errorBody != null) {
                            dialogMessage.value = errorBody
                        }
                        setFailureDialog.value = true
                        fetchInvitation()
                        fetchCards()
                        listJoinFamilyRequest()
                    }
                }else{
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "No token found"
                    )
                    dialogMessage.value = "Failed To Send Access"
                    setFailureDialog.value = true

                }

            }catch (e: Exception){
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Something went wrong, Try again Later"
                )
            }
        }

    }

    fun deleteCard(cardPublicId: String){

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
            )
            try {
                val tokenValue = userPreferences.tokenFlow.first()
                if(!tokenValue.isNullOrEmpty()){
                    val response = repository.deteleChildCard(tokenValue, cardPublicId)
                    if(response.isSuccessful){
                        closeDeleteCardDialog()
                        dialogMessage.value = "Card deleted Successfully"
                        setSuccessDialog.value = true
                        fetchInvitation()
                        fetchCards()
                    }else{
                        val errorBody = response.errorBody()?.string()

                        val errorDetail = errorBody?.let {
                            try {
                                JSONObject(it).getString("detail")
                            } catch (e: Exception) {
                                "Unknown error, Try again Later"
                            }
                        }

                        _uiState.value = _uiState.value.copy(
                            error = errorDetail,
                            isLoading = false

                        )
                        if (errorDetail != null) {
                            dialogMessage.value = errorDetail
                        }
                        setFailureDialog.value = true
                        fetchInvitation()
                        fetchCards()
                        listJoinFamilyRequest()
                    }

                }
            }catch (e:Exception){
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Something went wrong, Try again Later"
                )
            }
        }

    }

    fun setCardLimit(cardPublicId: String, limitAccessValue: String){
        viewModelScope.launch {
            setSuccessDialog.value = false
            _uiState.value = _uiState.value.copy(
                isLoading = true,
            )
            try {
                val tokenValue = userPreferences.tokenFlow.first()
                if(!tokenValue.isNullOrEmpty()){
                    val response = repository.setCardLimit(tokenValue, cardPublicId, limitAccessValue)
                    if(response.isSuccessful){
                        showSetLimitDialog.value = false
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                        )
                        dialogMessage.value = "Card Limit Set Successfully"
                        setSuccessDialog.value = true
                        fetchInvitation()
                        fetchCards()
                    }else {
                        val errorBody = response.errorBody()?.string()

                        val errorDetail = errorBody?.let {
                            try {
                                JSONObject(it).getString("detail")
                            } catch (e: Exception) {
                                "Unknown error, Try again Later"
                            }
                        }

                        _uiState.value = _uiState.value.copy(
                            error = errorDetail,
                            isLoading = false

                        )
                        if (errorDetail != null) {
                            dialogMessage.value = errorDetail
                        }
                        setFailureDialog.value = true
                        fetchInvitation()
                        fetchCards()
                        listJoinFamilyRequest()
                    }
                }
            }catch (e:Exception){
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Something went wrong, Try again Later"
                )
            }
        }
    }

    fun linkChildCard(childPublicId: String, relationship: String, allowTopUp: Boolean,
                      viewRemaining: Boolean,
                      viewHistory: Boolean,
                      freezeCard: Boolean){
        viewModelScope.launch {
            try{
                val tokenValue = userPreferences.tokenFlow.first()
                if(!tokenValue.isNullOrEmpty()){
                    val response = repository.linkCard(tokenValue, childPublicId, relationship, allowTopUp, viewRemaining, viewHistory, freezeCard )
                    if(response.isSuccessful){
                        dialogMessage.value = "Link Request Sent Successfully"
                        setSuccessDialog.value = true
                        closeLinkCardDialog()
                        fetchInvitation()
                        fetchCards()
                    }else{
                        val errorBody = response.errorBody()?.string()

                        val errorDetail = errorBody?.let {
                            try {
                                JSONObject(it).getString("detail")
                            } catch (e: Exception) {
                                "Unknown error, Try again Later"
                            }
                        }

                        _uiState.value = _uiState.value.copy(
                            error = errorDetail,
                            isLoading = false

                        )
                        if (errorDetail != null) {
                            dialogMessage.value = errorDetail
                        }
                        setFailureDialog.value = true
                        fetchInvitation()
                        listJoinFamilyRequest()
                        fetchCards()
                    }
                }
            }catch (e:Exception){
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Something went wrong, Try again Later"
                )
            }
        }
    }

    fun deleteLinkCardRequest(cardPublicId: String){

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
            )
            try {
                val tokenValue = userPreferences.tokenFlow.first()
                if(!tokenValue.isNullOrEmpty()){
                    val response = repository.deleteLinkRequest(tokenValue, cardPublicId)
                    if(response.isSuccessful){
                        closeDeleteCardDialog()
                        dialogMessage.value = "Link Request Deleted Successfully"
                        setSuccessDialog.value = true
                        fetchInvitation()
                        fetchCards()
                    }else{
                        val errorBody = response.errorBody()?.string()

                        val errorDetail = errorBody?.let {
                            try {
                                JSONObject(it).getString("detail")
                            } catch (e: Exception) {
                                "Unknown error, Try again Later"
                            }
                        }

                        _uiState.value = _uiState.value.copy(
                            error = errorDetail,
                            isLoading = false

                        )
                        dialogMessage.value = "Failed To Delete Link Request"
                        setFailureDialog.value = true
                        fetchInvitation()
                        listJoinFamilyRequest()
                        fetchCards()
                    }

                }
            }catch (e:Exception){
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Something went wrong, Try again Later"
                )
            }
        }

    }

    fun freezeCard(childPublicId: String){
        viewModelScope.launch {
            try {
                val tokenValue = userPreferences.tokenFlow.first()
                if(!tokenValue.isNullOrEmpty()){
                    val response = repository.freezeCard(tokenValue, childPublicId)
                    if(response.isSuccessful){
                        dialogMessage.value = "Card Frozen Successfully"
                        setSuccessDialog.value = true
                        fetchInvitation()
                        fetchCards()
                    }else{
                        val errorBody = response.errorBody()?.string()

                        val errorDetail = errorBody?.let {
                            try {
                                JSONObject(it).getString("detail")
                            } catch (e: Exception) {
                                "Unknown error, Try again Later"
                            }
                        }

                        _uiState.value = _uiState.value.copy(
                            error = errorDetail,
                            isLoading = false

                        )
                        dialogMessage.value = "Failed To Freeze Card"
                        setFailureDialog.value = true
                        fetchInvitation()
                        listJoinFamilyRequest()
                        fetchCards()
                    }

                }
            }catch (e:Exception){
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Something went wrong, Try again Later"
                )
            }
        }
    }

    fun unFreezeCard(childPublicId: String){
        viewModelScope.launch {
            try {
                val tokenValue = userPreferences.tokenFlow.first()
                if(!tokenValue.isNullOrEmpty()){
                    val response = repository.unfreezeCard(tokenValue, childPublicId)
                    if(response.isSuccessful){
                        dialogMessage.value = "Card Unfrozen Successfully"
                        setSuccessDialog.value = true
                        fetchInvitation()
                        fetchCards()
                    }else{
                        val errorBody = response.errorBody()?.string()

                        val errorDetail = errorBody?.let {
                            try {
                                JSONObject(it).getString("detail")
                            } catch (e: Exception) {
                                "Unknown error, Try again Later"
                            }
                        }

                        _uiState.value = _uiState.value.copy(
                            error = errorDetail,
                            isLoading = false

                        )
                        if (errorDetail != null) {
                            dialogMessage.value = errorDetail
                        }
                        setFailureDialog.value = true
                        fetchInvitation()
                        listJoinFamilyRequest()
                        fetchCards()
                    }

                }
            }catch (e:Exception){
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Something went wrong, Try again Later"
                )
            }
        }
    }


    fun getChildCardAvailableAccess(childPublicId: String){
        viewModelScope.launch {
            try {
                val tokenValue = userPreferences.tokenFlow.first()
                if(!tokenValue.isNullOrEmpty()){
                    val response = repository.getChildCardAvailableAccess(tokenValue, childPublicId)
                    if(response.isSuccessful){
                        _uiChildAvailableAccessState.value = _uiChildAvailableAccessState.value.copy(
                            child_public_id = childPublicId,
                            accessValue =  response.body()?.remaining ?: "0"
                        )
                    }else{
                        val errorBody = response.errorBody()?.string()

                        val errorDetail = errorBody?.let {
                            try {
                                JSONObject(it).getString("detail")
                            } catch (e: Exception) {
                                "Unknown error, Try again Later"
                            }
                        }

                        _uiState.value = _uiState.value.copy(
                            error = errorDetail,
                            isLoading = false

                        )

                        setFailureDialog.value = true
                        fetchInvitation()
                        fetchCards()
                        listJoinFamilyRequest()
                    }
                }
            }catch (e:Exception){
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Something went wrong, Try again Later"
                )

                setFailureDialog.value = true
            }
        }
    }

    private fun listJoinFamilyRequest(){
        viewModelScope.launch {
            _joinInvitationUiState.value = _joinInvitationUiState.value.copy(
                isLoading = true,
                error = null
            )

            try {
                val tokenValue = userPreferences.tokenFlow.first()
                if(!tokenValue.isNullOrEmpty()){

                    val response = repository.listJoinFamilyRequest(tokenValue)
                    if(response.isSuccessful){

                        val invitation = response.body()?.items ?: emptyList()
                        val total = response.body()?.total ?: 0
                        _joinInvitationUiState.value = _joinInvitationUiState.value.copy(
                            isLoading = false,
                            familyRequest =  invitation,
                            total = total,
                        )
                        if(invitation.isEmpty()){
                            _joinInvitationUiState.value = _joinInvitationUiState.value.copy(
                                isLoading = false
                            )
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()

                        val errorDetail = errorBody?.let {
                            try {
                                JSONObject(it).getString("detail")
                            } catch (e: Exception) {
                                "Unknown error, Try again Later"
                            }
                        }

                        _joinInvitationUiState.value = _joinInvitationUiState.value.copy(
                            error = errorDetail,
                            isLoading = false

                        )

                    }
                }else{
                    _joinInvitationUiState.value = _joinInvitationUiState.value.copy(
                        isLoading = false,
                        error = "No Token Found"
                    )
                }
            }catch (e:Exception){
                _joinInvitationUiState.value = _joinInvitationUiState.value.copy(
                    isLoading = false,
                    error = "Something went wrong, Try again Later"
                )

            }
        }
    }

    fun acceptFamilyInvitation(requesterPublicId: String){
        viewModelScope.launch {
            try {
                val tokenValue = userPreferences.tokenFlow.first()
                if(!tokenValue.isNullOrEmpty()){
                    val response = repository.AcceptFamilyInvitation(
                        tokenValue, requesterPublicId
                    )
                    if(response.isSuccessful){
                        dialogMessage.value = "Family Invitation Accepted Successfully"
                        setSuccessDialog.value = true
                        _joinInvitationUiState.value = _joinInvitationUiState.value.copy(
                            familyRequest = emptyList(),
                            total = 0
                        )
                        fetchInvitation()
                        fetchCards()
                        listJoinFamilyRequest()
                    }else{
                        val errorBody = response.errorBody()?.string()

                        val errorDetail = errorBody?.let {
                            try {
                                JSONObject(it).getString("detail")
                            } catch (e: Exception) {
                                "Unknown error, Try again Later"
                            }
                        }

                        _joinInvitationUiState.value = _joinInvitationUiState.value.copy(
                            error = errorDetail,
                            isLoading = false

                        )
                        dialogMessage.value = "Failed To Accept Invitation"
                        setFailureDialog.value = true

                        fetchInvitation()
                        fetchCards()
                        listJoinFamilyRequest()
                    }

                }
            }catch (e: Exception){
                _joinInvitationUiState.value = _joinInvitationUiState.value.copy(
                    error = "Something went wrong, Try again Later"
                )
            }
        }
    }

    fun declineFamilyInvitation(requesterPublicId: String){
        viewModelScope.launch {
            try {
                val tokenValue = userPreferences.tokenFlow.first()
                if(!tokenValue.isNullOrEmpty()){
                    val response = repository.DeclineFamilyInvitation(
                        tokenValue, requesterPublicId
                    )
                    if(response.isSuccessful){
                        dialogMessage.value = "Invitation Declined Successfully"
                        setSuccessDialog.value = true
                        _joinInvitationUiState.value = _joinInvitationUiState.value.copy(
                            familyRequest = emptyList(),
                            total = 0

                        )
                        fetchInvitation()
                        fetchCards()
                    }else{
                        val errorBody = response.errorBody()?.string()

                        val errorDetail = errorBody?.let {
                            try {
                                JSONObject(it).getString("detail")
                            } catch (e: Exception) {
                                "Unknown error, Try again Later"
                            }
                        }

                        _joinInvitationUiState.value = _joinInvitationUiState.value.copy(
                            error = errorDetail,
                            isLoading = false

                        )
                        dialogMessage.value = "Failed To Decline Invitation"
                        setFailureDialog.value = true

                        fetchInvitation()
                        fetchCards()
                        listJoinFamilyRequest()
                    }

                }
            }catch (e: Exception){

                Log.e("DECLINE_ERROR", "Error", e)

                _joinInvitationUiState.value = _joinInvitationUiState.value.copy(
                    error = "Something went wrong, Try again Later"
                )

            }
        }
    }


    fun openSendDialog(card: RelationshipItem){
        selectedCard.value = card
        showSendDialog.value = true
    }

    fun closeSendDialog(){
        selectedCard.value = null
        showSendDialog.value = false
    }

    fun openSendConfirmDialog(card: RelationshipItem){
        selectedCard.value = card
        showSendConfirmDialog.value = true
    }

    fun closeSendConfirmDialog(){
        selectedCard.value = null
        showSendConfirmDialog.value = false
    }

    fun openDeleteCardDialog(card: RelationshipItem){
        selectedCard.value = card
        showDeleteCardDialog.value = true
    }

    fun closeDeleteCardDialog(){
        selectedCard.value  = null
        showDeleteCardDialog.value = false
    }

    fun openSetLimitDialog(card: RelationshipItem){
        selectedCard.value = card
        showSetLimitDialog.value = true
    }

    fun closeSetLimitDialog(){
        selectedCard.value = null
        showSetLimitDialog.value = false
    }

    fun openSuccessDialog(){
        setSuccessDialog.value = true
    }

    fun closeSuccessDialog(){
        setSuccessDialog.value = false

    }

    fun closeFailureDialog(){
        setFailureDialog.value = false
    }

    fun showChildCardBalance(card: RelationshipItem){
        selectedCard.value = card
        showChildCardAvailableAccess.value = true
    }

    fun hideChildCardBalance(){
        selectedCard.value = null
        showChildCardAvailableAccess.value = false
        _uiChildAvailableAccessState.value = _uiChildAvailableAccessState.value.copy(
            child_public_id = "",
            accessValue =  ""
        )

    }

}