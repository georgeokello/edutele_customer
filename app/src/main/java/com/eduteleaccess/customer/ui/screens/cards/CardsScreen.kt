package com.eduteleaccess.customer.ui.screens.cards

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.eduteleaccess.customer.R
import com.eduteleaccess.customer.data.local.UserPreferences
import com.eduteleaccess.customer.data.model.cards.FamilyContactItems
import com.eduteleaccess.customer.data.model.cards.LinkCardInvitationItems
import com.eduteleaccess.customer.data.model.cards.RelationshipItem
import com.eduteleaccess.customer.ui.components.AppTemplate
import com.eduteleaccess.customer.ui.components.AppTemplateViewModel
import com.eduteleaccess.customer.ui.components.AppTemplateViewModelFactory
import com.eduteleaccess.customer.ui.navigation.navItems
import com.eduteleaccess.customer.ui.util.DismissFailureDialog
import com.eduteleaccess.customer.ui.util.DismissSuccessDialog
import com.eduteleaccess.customer.ui.util.extractInitials
import com.eduteleaccess.customer.ui.util.navigateTo

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CardsScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val userPreferences = remember { UserPreferences(context) }

    val viewModel: CardsViewModel = viewModel(
        factory = CardsViewModelFactory(userPreferences)
    )

    val state by viewModel.uiState.collectAsState()
    val showSendDialog by viewModel.showSendDialog.collectAsState()
    val showSendConfirmDialog by viewModel.showSendConfirmDialog.collectAsState()
    val showSetLimitDialog by viewModel.showSetLimitDialog.collectAsState()

    val invitationState by viewModel.invitationUiState.collectAsState()

    val joinFamilyInvitation by viewModel.joinInvitationUiState.collectAsState()

    val showDeleteCardDialog by viewModel.showDeleteCardDialog.collectAsState()
    
    val successDialog by viewModel.setSuccessDialog.collectAsState()

    val dialogMessage by viewModel.dialogMessage.collectAsState()

    val failureDialog by viewModel.setFailureDialog.collectAsState()

    val stateLinkCard by viewModel.uiLinkState.collectAsState()

    val accessChildAvailableAccess by viewModel.uiChildAvailableAccessState.collectAsState()

    val showChildCardAvailableAccess by viewModel.showChildCardAvailableAccess.collectAsState()

    val appViewModel: AppTemplateViewModel = viewModel(
        factory = AppTemplateViewModelFactory(userPreferences)
    )

    val cardInfo by appViewModel.cardInfoUiState.collectAsState()

    val logoutState by appViewModel.logoutState.collectAsState()

    val selectedCard by viewModel.selectedCard.collectAsState()

    val user by viewModel.username.collectAsState()
    val currentRoute = "cards"

    var accessValue by remember { mutableStateOf("") }
    var remarks by remember { mutableStateOf("") }
    var pin by remember {
        mutableStateOf("")
    }
    var limitAccessValue by remember {
        mutableStateOf("")
    }

    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.init()
    }

    AppTemplate(
        userName = user.toString(),
        availableAccess = " ${cardInfo.card?.remaining ?: 0}",
        navItems = navItems,
        selectedNavIndex = navItems.indexOfFirst { it.route == currentRoute },
        onNavSelected = { index ->
            val route = navItems[index].route
            if (route != currentRoute) navigateTo(navController, route)
        },
        navController = navController
    ) {

        Box(modifier = Modifier.fillMaxSize()) {

            // MAIN CONTENT
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(42.dp))
                Text(text = "Manage Family Cards")
                Spacer(modifier = Modifier.height(5.dp))

                when{
                    invitationState.isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                    }
                    else -> {
                        if(invitationState.total != 0){
                            Text(text = "Pending Request (${invitationState.total})")
                        }

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(invitationState.invitations){ invitation ->
                                InvitationCard(invitation, viewModel)
                            }
                        }
                    }
                }

                when{
                    joinFamilyInvitation.isLoading -> {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = Color.White)
                    }
                    else -> {
                        if(joinFamilyInvitation.total != 0){
                            Text(text = "Pending Request (${joinFamilyInvitation.total})")
                        }
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(joinFamilyInvitation.familyRequest){ invitation ->
                                JoinFamilyInvitationCard(invitation, viewModel )
                            }
                        }
                    }
                }

                when {
                    state.isLoading -> {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = Color.White)
                    }

                    state.error != null -> {
                        LaunchedEffect(state.error) {

                            if (state.error == "Invalid or expired token.") {

                                navController.navigate("login") {
                                    popUpTo(0)
                                    launchSingleTop = true
                                }
                            }
                        }
                        Text(
                            text = "${state.error}",
                            color = Color.Gray
                        )
                    }
                    state.message.isNotEmpty() -> {
                        Text(
                            text = state.message,
                        )
                    }

                    else -> {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.cards) { card ->

                                when (card.i_manage_them.status) {

                                    "pending" -> {
                                        PendingChildCard(card, viewModel)
                                    }

                                    "active" -> {

                                        ChildCard(
                                            card,
                                            viewModel,
                                            navController,
                                            showChildCardAvailableAccess,
                                            accessChildAvailableAccess
                                        )


                                    }

                                    else -> {
                                        ShowFamilyCards(card, viewModel)
                                    }
                                }


                            }
                        }

                        if(showSendDialog && selectedCard != null){
                            AlertDialog(

                                onDismissRequest = {
                                    viewModel.closeSendDialog()
                                },
                                shape = RoundedCornerShape(8.dp),

                                title = {

                                    Column {

                                        Text(
                                            text = "Send Access",
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.Bold
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = "Complete the details below",
                                            color = Color.Gray,
                                            fontSize = 13.sp
                                        )
                                    }
                                },

                                text = {

                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {

                                        // Recipient card
                                        Card(
                                            colors = CardDefaults.cardColors(
                                                containerColor = Color(0xFFF5F7FA)
                                            ),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {

                                            Column(
                                                modifier = Modifier.padding(12.dp)
                                            ) {

                                                Text(
                                                    text = "Recipient",
                                                    color = Color.Gray,
                                                    fontSize = 12.sp
                                                )

                                                Text(
                                                    text = selectedCard!!.full_name,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 16.sp
                                                )

                                            }
                                        }

                                        OutlinedTextField(
                                            value = accessValue,
                                            onValueChange = {
                                                if (it.all(Char::isDigit)) {
                                                    accessValue = it
                                                }
                                            },
                                            label = {
                                                Text("Value (UGX)")
                                            },
                                            modifier = Modifier.fillMaxWidth(),
                                            keyboardOptions = KeyboardOptions(
                                                keyboardType = KeyboardType.Number
                                            ),
                                            singleLine = true,
                                            shape = RoundedCornerShape(8.dp)
                                        )

                                        OutlinedTextField(
                                            value = remarks,
                                            onValueChange = {
                                                remarks = it
                                            },
                                            label = {
                                                Text("Remarks")
                                            },
                                            modifier = Modifier.fillMaxWidth(),
                                            maxLines = 2,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                    }
                                },

                                confirmButton = {

                                    Button(
                                        onClick = {
                                            viewModel.closeSendDialog()
                                            // Handle confirmation
                                            viewModel.openSendConfirmDialog(selectedCard!!)
                                            // clear fields
                                            accessValue = ""
                                            remarks = ""

                                        },
                                        enabled =
                                        accessValue.isNotBlank(),

                                        shape = RoundedCornerShape(6.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF2E7D32),
                                            contentColor = Color.White
                                        )
                                    ) {

                                        Text(
                                            text = "Send Access",
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                },

                                dismissButton = {

                                    OutlinedButton(
                                        onClick = {
                                            viewModel.closeSendDialog()
                                        },
                                        shape = RoundedCornerShape(6.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF990000),
                                            contentColor = Color.White
                                        )
                                    ) {

                                        Text(
                                            "Cancel",
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            )


                        }

                        if(showSendConfirmDialog && selectedCard != null){
                            AlertDialog(
                                onDismissRequest = { /*TODO*/ },
                                title = {
                                    Text(text = "Confirm Transfer of $accessValue")
                                },
                                shape = RoundedCornerShape(8.dp),
                                text = {

                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {

                                        // Recipient card
                                        Card(
                                            colors = CardDefaults.cardColors(
                                                containerColor = Color(0xFFF5F7FA)
                                            ),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {

                                            Column(
                                                modifier = Modifier.padding(12.dp)
                                            ) {

                                                Text(
                                                    text = "Recipient",
                                                    color = Color.Gray,
                                                    fontSize = 12.sp
                                                )

                                                Text(
                                                    text = selectedCard!!.full_name,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 16.sp
                                                )

                                            }
                                        }

                                        OutlinedTextField(
                                            value = pin,
                                            onValueChange = {
                                                if (it.all(Char::isDigit) && it.length <= 6) {
                                                    pin = it
                                                }
                                            },
                                            label = {
                                                Text("PIN")
                                            },
                                            modifier = Modifier.fillMaxWidth(),
                                            keyboardOptions = KeyboardOptions(
                                                keyboardType = KeyboardType.NumberPassword
                                            ),
                                            singleLine = true,
                                            shape = RoundedCornerShape(8.dp),
                                            visualTransformation = if (passwordVisible) {
                                                VisualTransformation.None
                                            } else {
                                                PasswordVisualTransformation()
                                            },
                                            trailingIcon = {
                                                IconButton(
                                                    onClick = { passwordVisible = !passwordVisible }
                                                ) {
                                                    Icon(
                                                        painter = painterResource(
                                                            id = if (passwordVisible)
                                                                R.drawable.visibility_off_24px
                                                            else
                                                                R.drawable.visibility_24px
                                                        ),
                                                        contentDescription = "Toggle PIN visibility",
                                                        tint = Color.Gray
                                                    )
                                                }
                                            }
                                        )
                                    }
                                },
                                confirmButton = {

                                    Button(
                                        onClick = {
                                            // Handle confirmation
                                            viewModel.sendAccess(selectedCard!!.public_id, accessValue, pin, remarks)
                                            //clear fields
                                            pin = ""
                                        },
                                        enabled = pin.length >= 4,

                                        shape = RoundedCornerShape(6.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF2E7D32),
                                            contentColor = Color.White
                                        )
                                    ) {

                                        Text(
                                            text = "Send Access",
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                },
                                dismissButton = {
                                    OutlinedButton(
                                        onClick = {
                                            viewModel.closeSendConfirmDialog()
                                        },
                                        shape = RoundedCornerShape(6.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF990000),
                                            contentColor = Color.White
                                        )
                                    ) {

                                        Text(
                                            "Cancel",
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                }
                            )
                        }

                        if(showDeleteCardDialog && selectedCard != null){
                            AlertDialog(
                                onDismissRequest = { /*TODO*/ },
                                title = {
                                    Text(text = "Delete Card: ${selectedCard!!.full_name}")
                                },
                                text = {
                                    Text(text = "This action cannot be undone!")
                                },
                                confirmButton = {

                                    Button(
                                        onClick = {
                                            viewModel.deleteCard(selectedCard!!.public_id)
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFFD32F2F),
                                            contentColor = Color.White
                                        )
                                    ) {

                                        Text("Delete Card")
                                    }
                                },
                                dismissButton = {

                                    OutlinedButton(
                                        onClick = {
                                            viewModel.closeDeleteCardDialog()
                                        }
                                    ) {

                                        Text("Cancel")
                                    }
                                }
                            )
                        }

                        if(showSetLimitDialog && selectedCard != null){
                            AlertDialog(
                                onDismissRequest = { /*TODO*/ },
                                title = {
                                    Text(text = "Weekly Usage Limit")
                                },
                                shape = RoundedCornerShape(8.dp),
                                text = {

                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {

                                        Text(text = selectedCard!!.full_name)

                                        OutlinedTextField(
                                            value = limitAccessValue,
                                            onValueChange = {
                                                limitAccessValue = it
                                            },
                                            label = {
                                                Text("Max spend per week (UGX)")
                                            }
                                        )

                                        Text(text = "Leave empty to remove the limit.")
                                    }
                                },
                                confirmButton = {

                                    Button(
                                        onClick = {
                                            viewModel.setCardLimit(selectedCard!!.public_id, limitAccessValue)
                                            // clears fields
                                            limitAccessValue = ""
                                        },
                                        enabled = limitAccessValue.isNotBlank(),

                                        shape = RoundedCornerShape(6.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF2E7D32),
                                            contentColor = Color.White
                                        )
                                    ) {

                                        Text(
                                            text = "Save",
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                },
                                dismissButton = {
                                    OutlinedButton(
                                        onClick = {
                                            viewModel.closeSetLimitDialog()
                                        },
                                        shape = RoundedCornerShape(6.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF990000),
                                            contentColor = Color.White
                                        )
                                    ) {

                                        Text(
                                            "Cancel",
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                }
                            )
                        }

                    }
                }
            }

            DismissSuccessDialog(
                showDialog = successDialog,
                message = dialogMessage,
                onDismiss = {
                    viewModel.closeSuccessDialog()
                }
            )

            DismissFailureDialog(
                showDialog = failureDialog,
                message = dialogMessage,
                onDismiss = {
                    viewModel.closeFailureDialog()
                }
            )


            if(stateLinkCard.showDialog){
                selectedCard?.let { card ->
                    LinkFamilyMemberDialog(
                        card = card,
                        relationship = stateLinkCard.relationship,

                        allowTopUp = stateLinkCard.allowTopUp,
                        viewBalance = stateLinkCard.viewBalance,
                        viewHistory = stateLinkCard.viewHistory,
                        freezeCard = stateLinkCard.freezeCard,

                        relationshipOptions = stateLinkCard.relationshipOptions,

                        onRelationshipChange = viewModel::updateRelationship,

                        onAllowTopUpChange = viewModel::updateAllowTopUp,
                        onViewAccessValueChange = viewModel::updateViewAvailableAccess,
                        onViewHistoryChange = viewModel::updateViewHistory,
                        onFreezeCardChange = viewModel::updateFreezeCard,

                        onDismiss = {
                            viewModel.closeLinkCardDialog()
                        },
                        onConfirm = {
                            viewModel.linkChildCard(
                                card.public_id,
                                stateLinkCard.relationship,
                                stateLinkCard.allowTopUp,
                                stateLinkCard.viewBalance,
                                stateLinkCard.viewHistory,
                                stateLinkCard.freezeCard
                            )
                        }
                    )

                }
            }


        }
    }
}


@Composable
fun ShowFamilyCards(card: RelationshipItem, viewModel: CardsViewModel) {

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Column {

            // TOP SECTION
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 20.dp,
                        vertical = 20.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Avatar
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0099C8)),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = extractInitials(card.full_name ?: ""),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                // User Details
                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = card.full_name ?: "",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF14213D)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = card.phone ?: "Contact not set",
                        fontSize = 16.sp,
                        color = Color(0xFF5A6C8F)
                    )

                    Spacer(modifier = Modifier.height(4.dp))


                }

                // Manage Icon
                IconButton(
                    onClick = {
                        // Send Link request
                        viewModel.openLinkCardDialog(card)
                    },
                    modifier = Modifier
                        .background(
                            color = Color(0xFFCE8D00),
                            shape = CircleShape
                        )
                ) {

                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }


        }
    }
}

@Composable
fun PendingChildCard(card: RelationshipItem, viewModel: CardsViewModel) {

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Column {

            // TOP SECTION
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 20.dp,
                        vertical = 20.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Avatar
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0099C8)),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = extractInitials(card.full_name ?: ""),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                // User Details
                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = card.full_name ?: "",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF14213D)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = card.phone ?: "Contact not set",
                        fontSize = 16.sp,
                        color = Color(0xFF5A6C8F)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Waiting for Acceptance",
                        fontSize = 16.sp,
                        color = Color(0xFFCE8D00)
                    )
                }

                // Delete Icon
                IconButton(
                    onClick = {
                        viewModel.deleteLinkCardRequest(card.public_id)
                    }
                ) {

                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = Color.Red
                    )
                }
            }

        }
    }
}

@Composable
fun ChildCard(card: RelationshipItem, viewModel: CardsViewModel, navController: NavController, showChildAvailableAccess: Boolean, childCardAvailableAccessUiState: ChildCardAvailableAccessUiState) {

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Column {

            // TOP SECTION
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 20.dp,
                        vertical = 20.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Avatar
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0099C8)),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = extractInitials(card.full_name ?: ""),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                // User Details
                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = card.full_name ?: "",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF14213D)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = card.phone ?: "Contact not set",
                        fontSize = 16.sp,
                        color = Color(0xFF5A6C8F)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${card.i_manage_them.relationship_type ?: ""} · ${card.account_type ?: ""}",
                        fontSize = 14.sp,
                        color = Color(0xFF8D99AE)
                    )

                    HorizontalDivider(
                        color = Color(0xFFF0F0F0),
                        thickness = 1.dp
                    )

                    Text(
                        text = "Weekly Limit: ${card.i_manage_them.weekly_spend_limit ?: ""}",
                        fontSize = 14.sp,
                        color = Color(0xFF8D99AE),
                        modifier = Modifier.padding(top = 5.dp)
                    )

                }

                // Delete Icon
                IconButton(
                    onClick = {
                        // delete card
                        viewModel.openDeleteCardDialog(card)
                    }
                ) {

                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = Color.Red
                    )
                }
            }
            HorizontalDivider(
                color = Color(0xFFF0F0F0),
                thickness = 1.dp
            )

            // AMOUNT
            if(showChildAvailableAccess && card.public_id == childCardAvailableAccessUiState.child_public_id){
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 20.dp,
                            vertical = 20.dp,
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Balance",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF8D99AE)
                    )
                    Text(
                        text = "UGX ${childCardAvailableAccessUiState.accessValue ?: 0}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

            }

            HorizontalDivider(
                color = Color(0xFFF0F0F0),
                thickness = 1.dp
            )

            // ACTIONS
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
            ) {

                if(!card.card_frozen){
                    ActionItem(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.KeyboardArrowUp,
                        text = "Send",
                        color = Color(0xFF00A651),
                        onClick = {
                            // send money to the card
                            viewModel.openSendDialog(card)
                        }
                    )
                }

                VerticalDivider()
                if(!card.card_frozen){
                    if(showChildAvailableAccess && card.public_id == childCardAvailableAccessUiState.child_public_id){
                        ActionItem(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.CheckCircle,
                            text = "Hide",
                            color = Color(0xFF0057FF),
                            onClick = {
                                // hide balance
                                viewModel.hideChildCardBalance()
                            }
                        )
                    }else{
                        ActionItem(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.CheckCircle,
                            text = "Show",
                            color = Color(0xFF0057FF),
                            onClick = {
                                // show and hide balance
                                viewModel.showChildCardBalance(card)
                                viewModel.getChildCardAvailableAccess(card.public_id)
                            }
                        )
                    }

                }

                VerticalDivider()

                ActionItem(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.DateRange,
                    text = "History",
                    color = Color(0xFF4B5563),
                    onClick = {
                        // show transaction history
                        navigateTo(navController, "childCardHistory/${card.public_id}")
                    }
                )

                VerticalDivider()

                if(card.i_manage_them.can_freeze){
                    if(card.card_frozen){
                        ActionItem(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Lock,
                            text = "UnFreeze",
                            color = Color(0xFFCE8D00),
                            onClick = {
                                // unfreeze card
                                viewModel.unFreezeCard(card.public_id)
                            }
                        )
                    }else{
                        ActionItem(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Lock,
                            text = "Freeze",
                            color = Color(0xFFFF1E38),
                            onClick = {
                                // freeze card
                                viewModel.freezeCard(card.public_id)
                            }
                        )
                    }
                }

            }

            HorizontalDivider(
                color = Color(0xFFF0F0F0),
                thickness = 1.dp
            )

            // BOTTOM SECTION
            if(!card.card_frozen){
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            // limit amount to spend
                            viewModel.openSetLimitDialog(card)
                        }
                        .padding(
                            horizontal = 18.dp,
                            vertical = 16.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFFCE8D00),
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = "Set weekly limit",
                        color = Color(0xFFCE8D00),
                        fontSize = 16.sp,
                        modifier = Modifier.weight(1f)
                    )

                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8)
                    )
                }
            }

        }
    }
}



@Composable
fun ActionItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    text: String,
    color: Color,
    onClick: () -> Unit
) {

    Column(
        modifier = modifier
            .fillMaxHeight()
            .clickable {
                onClick()
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = text,
            color = color,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}


@Composable
fun VerticalDivider() {

    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(1.dp)
            .background(Color(0xFFF0F0F0))
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinkFamilyMemberDialog(

    card: RelationshipItem,

    relationship: String,

    allowTopUp: Boolean,
    viewBalance: Boolean,
    viewHistory: Boolean,
    freezeCard: Boolean,

    relationshipOptions: List<String>,

    onRelationshipChange: (String) -> Unit,

    onAllowTopUpChange: (Boolean) -> Unit,
    onViewAccessValueChange: (Boolean) -> Unit,
    onViewHistoryChange: (Boolean) -> Unit,
    onFreezeCardChange: (Boolean) -> Unit,

    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(8.dp),
        containerColor = Color.White,

        confirmButton = {},

        title = {
            Text(
                text = "Send Manage Request to ${card.full_name ?: ""}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )
        },

        text = {
            Column {

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = Color(0xFFE5E7EB)
                )

                Spacer(Modifier.height(12.dp))

                // PHONE NUMBER
                Text(
                    card.phone ?: "",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = Color(0xFF374151)
                )

                Spacer(Modifier.height(24.dp))

                // RELATIONSHIP
                Text(
                    "Your Relationship",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = Color(0xFF374151)
                )

                Spacer(Modifier.height(10.dp))

                RelationshipDropdown(
                    value = relationship,
                    options = relationshipOptions,
                    onValueChange = onRelationshipChange
                )

                Spacer(Modifier.height(28.dp))

                // PERMISSIONS
                Text(
                    "Permissions",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF374151)
                )

                Spacer(Modifier.height(14.dp))

                PermissionItem(
                    text = "Allow Top Up",
                    checked = allowTopUp,
                    onCheckedChange = onAllowTopUpChange
                )

                PermissionItem(
                    text = "View Balance",
                    checked = viewBalance,
                    onCheckedChange = onViewAccessValueChange
                )

                PermissionItem(
                    text = "View Transaction History",
                    checked = viewHistory,
                    onCheckedChange = onViewHistoryChange
                )

                PermissionItem(
                    text = "Freeze / Unfreeze Card",
                    checked = freezeCard,
                    onCheckedChange = onFreezeCardChange
                )

                Spacer(Modifier.height(28.dp))

                // BUTTONS
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {

                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2563EB),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Link Member")
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RelationshipDropdown(
    value: String,
    options: List<String>,
    onValueChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {

        OutlinedTextField(
            value = value.ifEmpty { "Select relationship" },
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            },
            shape = RoundedCornerShape(16.dp)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item) },
                    onClick = {
                        onValueChange(item)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun PermissionItem(text: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )

        Spacer(Modifier.width(8.dp))

        Text(
            text = text,
            fontSize = 16.sp,
            color = Color(0xFF374151)
        )
    }
}

@Composable
fun InvitationCard(invitations:LinkCardInvitationItems, viewModel: CardsViewModel) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF1F5F9)
        ),
        border = BorderStroke(
            width = 1.dp,
            color = Color(0xFFBFDBFE)
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {

            // TOP SECTION
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                // PROFILE ICON
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE91E63)),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = extractInitials(invitations.parent_name),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                // USER INFO
                Column {

                    Text(
                        text = invitations.parent_name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${invitations.parent_phone} · ${invitations.relationship_type}",
                        fontSize = 14.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // BUTTONS BELOW EVERYTHING
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {

                OutlinedButton(
                    onClick = {
                        viewModel.declineInvitation(invitations.relationship_id)
                    },
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(
                        1.dp,
                        Color(0xFFD1D5DB)
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF374151)
                    )
                ) {

                    Text(
                        text = "Decline",
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Button(
                    onClick = {
                        viewModel.acceptInvitation(invitations.relationship_id)
                    },
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2563EB),
                        contentColor = Color.White
                    )
                ) {

                    Text(
                        text = "Accept",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun JoinFamilyInvitationCard(familyRequest: FamilyContactItems, viewModel: CardsViewModel) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF1F5F9)
        ),
        border = BorderStroke(
            width = 1.dp,
            color = Color(0xFFBFDBFE)
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {

            // TOP SECTION
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                // PROFILE ICON
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE91E63)),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = extractInitials(familyRequest.full_name),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                // USER INFO
                Column {

                    Text(
                        text = "${familyRequest.full_name} wants to add you as a family contact",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = familyRequest.phone,
                        fontSize = 14.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // BUTTONS BELOW EVERYTHING
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {

                OutlinedButton(
                    onClick = {
                       // decline
                        viewModel.declineFamilyInvitation(familyRequest.requester_public_id)
                    },
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(
                        1.dp,
                        Color(0xFFD1D5DB)
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF374151)
                    )
                ) {

                    Text(
                        text = "Decline",
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Button(
                    onClick = {
                        // Accept
                        viewModel.acceptFamilyInvitation(familyRequest.requester_public_id)
                    },
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2563EB),
                        contentColor = Color.White
                    )
                ) {

                    Text(
                        text = "Accept",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}