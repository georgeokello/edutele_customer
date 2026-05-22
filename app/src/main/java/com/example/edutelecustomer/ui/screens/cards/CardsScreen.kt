package com.example.edutelecustomer.ui.screens.cards

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowLeft
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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.edutelecustomer.data.local.UserPreferences
import com.example.edutelecustomer.data.model.cards.LinkCardInvitationItems
import com.example.edutelecustomer.data.model.cards.LinkedCardItem
import com.example.edutelecustomer.data.model.transactions.TransactionItem
import com.example.edutelecustomer.ui.components.AppTemplate
import com.example.edutelecustomer.ui.components.AppTemplateViewModel
import com.example.edutelecustomer.ui.components.AppTemplateViewModelFactory
import com.example.edutelecustomer.ui.navigation.navItems
import com.example.edutelecustomer.ui.screens.historyscreen.HistoryViewModel
import com.example.edutelecustomer.ui.screens.historyscreen.TransactionCard
import com.example.edutelecustomer.ui.util.extractInitials
import com.example.edutelecustomer.ui.util.navigateTo
import kotlinx.coroutines.delay

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

    val showDeleteCardDialog by viewModel.showDeleteCardDialog.collectAsState()
    
    val SuccessDialog by viewModel.setSuccessDialog.collectAsState()

    val stateLinkCard by viewModel.uiLinkState.collectAsState()

    val accessChildBalance by viewModel.uiChildBalanceState.collectAsState()

    val showChildCardBalance by viewModel.showChildCardBalance.collectAsState()

    val appViewModel: AppTemplateViewModel = viewModel(
        factory = AppTemplateViewModelFactory(userPreferences)
    )

    val cardInfo by appViewModel.cardInfoUiState.collectAsState()

    val user by viewModel.username.collectAsState()
    val currentRoute = "cards"

    var amount by remember { mutableStateOf("") }
    var remarks by remember { mutableStateOf("") }
    var pin by remember {
        mutableStateOf("")
    }
    var limitAmount by remember {
        mutableStateOf("")
    }

    LaunchedEffect(Unit) {
        viewModel.init()
    }

    AppTemplate(
        userName = user.toString(),
        balance = " ${cardInfo.card?.balance ?: 0}",
        navItems = navItems,
        selectedNavIndex = navItems.indexOfFirst { it.route == currentRoute },
        onNavSelected = { index ->
            val route = navItems[index].route
            if (route != currentRoute) navigateTo(navController, route)
        }
    ) {

        Box(modifier = Modifier.fillMaxSize()) {

            // MAIN CONTENT
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(40.dp))

                when{
                    invitationState.isLoading -> {
                        CircularProgressIndicator()
                    }
                    else -> {
                        Text(text = "Pending Request (${invitationState.total})")
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(invitationState.invitations){ invitation ->
                                InvitationCard(invitation, viewModel)
                            }
                        }
                    }
                }

                when {
                    state.isLoading -> {
                        CircularProgressIndicator()
                    }

                    state.error != null -> {
                        Text(
                            text = state.error ?: "Unknown error",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    state.message.isNotEmpty() -> {
                        Text(
                            text = state.message,
                        )
                    }

                    else -> {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.cards) { card ->
                                ChildCard(card, viewModel, navController, showChildCardBalance, accessChildBalance)

                                if(showSendDialog){
                                    AlertDialog(

                                        onDismissRequest = {
                                            viewModel.closeSendDialog()
                                        },

                                        title = {
                                            Text("Sending Money TO:")
                                        },

                                        text = {

                                            Column(
                                                verticalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {

                                                Text(text = card.full_name)

                                                OutlinedTextField(
                                                    value = amount,
                                                    onValueChange = {
                                                        amount = it
                                                    },
                                                    label = {
                                                        Text("Amount")
                                                    }
                                                )

                                                OutlinedTextField(
                                                    value = remarks,
                                                    onValueChange = {
                                                        remarks = it
                                                    },
                                                    label = {
                                                        Text("Remarks")
                                                    }
                                                )
                                            }
                                        },

                                        confirmButton = {

                                            Button(
                                                onClick = {

                                                    // Handle confirmation
                                                    viewModel.openSendConfirmDialog()

                                                    viewModel.closeSendDialog()
                                                }
                                            ) {

                                                Text("Send")
                                            }
                                        },

                                        dismissButton = {

                                            OutlinedButton(
                                                onClick = {
                                                    viewModel.closeSendDialog()
                                                }
                                            ) {

                                                Text("Cancel")
                                            }
                                        }
                                    )
                                }

                                if(showSendConfirmDialog){
                                    AlertDialog(
                                        onDismissRequest = { /*TODO*/ },
                                        title = {
                                            Text(text = "Comfirm Transfer")
                                        },
                                        text = {

                                            Column(
                                                verticalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {

                                                Text(text = card.full_name)

                                                OutlinedTextField(
                                                    value = pin,
                                                    onValueChange = {
                                                        pin = it
                                                    },
                                                    label = {
                                                        Text("Enter Pin")
                                                    }
                                                )
                                            }
                                        },
                                        confirmButton = {

                                            Button(
                                                onClick = {
                                                    // Handle confirmation
                                                    viewModel.SendMoney(card.child_public_id, amount, pin, remarks)
                                                }
                                            ) {

                                                Text("Confirm")
                                            }
                                        },
                                        dismissButton = {

                                            OutlinedButton(
                                                onClick = {
                                                    viewModel.closeSendConfirmDialog()
                                                }
                                            ) {

                                                Text("Cancel")
                                            }
                                        }
                                    )
                                }

                                if(showDeleteCardDialog){
                                    AlertDialog(
                                        onDismissRequest = { /*TODO*/ },
                                        title = {
                                            Text(text = "Delete Card: ${card.full_name}")
                                        },
                                        text = {
                                            Text(text = "This action cannot be undone!")
                                        },
                                        confirmButton = {

                                            Button(
                                                onClick = {
                                                    viewModel.deleteCard(card.child_public_id)
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

                                if(showSetLimitDialog){
                                    AlertDialog(
                                        onDismissRequest = { /*TODO*/ },
                                        title = {
                                            Text(text = "Weekly Spending Limit")
                                        },
                                        text = {

                                            Column(
                                                verticalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {

                                                Text(text = card.full_name)

                                                OutlinedTextField(
                                                    value = limitAmount,
                                                    onValueChange = {
                                                        limitAmount = it
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
                                                    // Handle confirmation
                                                    viewModel.setCardLimit(card.child_public_id, limitAmount)
                                                }
                                            ) {

                                                Text("Save")
                                            }
                                        },
                                        dismissButton = {

                                            OutlinedButton(
                                                onClick = {
                                                    viewModel.closeSetLimitDialog()
                                                }
                                            ) {

                                                Text("Cancel")
                                            }
                                        }
                                    )
                                }

                            }
                        }

                    }
                }
            }

            AutoDismissSuccessDialog(
                showDialog = SuccessDialog,
                message = "",
                onDismiss = {
                    viewModel.closeSuccessDialog()
                }
            )

            // TOP RIGHT (Example action button)
            FloatingActionButton(
                onClick = {
                    viewModel.openLinkCardDialog()
                },
                containerColor = Color(0xFFA57100),
                contentColor = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add"
                )
            }

            LinkFamilyMemberDialog(
                    showDialog = stateLinkCard.showDialog,

                    phoneNumber = stateLinkCard.phoneNumber,
                    relationship = stateLinkCard.relationship,

                    allowTopUp = stateLinkCard.allowTopUp,
                    viewBalance = stateLinkCard.viewBalance,
                    viewHistory = stateLinkCard.viewHistory,
                    freezeCard = stateLinkCard.freezeCard,

                    relationshipOptions = stateLinkCard.relationshipOptions,

                    onPhoneNumberChange = viewModel::updatePhone,
                    onRelationshipChange = viewModel::updateRelationship,

                    onAllowTopUpChange = viewModel::updateAllowTopUp,
                    onViewBalanceChange = viewModel::updateViewBalance,
                    onViewHistoryChange = viewModel::updateViewHistory,
                    onFreezeCardChange = viewModel::updateFreezeCard,

                    onDismiss = {
                        viewModel.closeLinkCardDialog()
                    },
                    onConfirm = {
                        viewModel.LinkChildCard(
                            stateLinkCard.phoneNumber,
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

@Composable
fun ChildCard(card: LinkedCardItem, viewModel: CardsViewModel, navController: NavController, showChildBalance: Boolean, childCardBalanceUiState: ChildCardBalanceUiState) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
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
                        text = extractInitials(card.full_name),
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                // User Details
                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = card.full_name,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF14213D)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = card.phone,
                        fontSize = 18.sp,
                        color = Color(0xFF5A6C8F)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${card.relationship_type} · ${card.account_type}",
                        fontSize = 16.sp,
                        color = Color(0xFF8D99AE)
                    )
                }

                // Delete Icon
                IconButton(
                    onClick = {
                        // delete card
                        viewModel.openDeleteCardDialog()
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
            if(showChildBalance){
                Text(
                    text = "UGX ${childCardBalanceUiState.balance}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
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

                ActionItem(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.KeyboardArrowUp,
                    text = "Send",
                    color = Color(0xFF00A651),
                    onClick = {
                        // send money to the card
                        viewModel.openSendDialog()
                    }
                )

                VerticalDivider()

                if(showChildBalance){
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
                        text = "Balance",
                        color = Color(0xFF0057FF),
                        onClick = {
                            // show and hide balance
                            viewModel.showChildCardBalance()
                            viewModel.getChildCardBalance(card.child_public_id)
                        }
                    )
                }

                VerticalDivider()

                ActionItem(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.DateRange,
                    text = "History",
                    color = Color(0xFF4B5563),
                    onClick = {
                        // show transaction history
                        navigateTo(navController, "childCardHistory/${card.child_public_id}")
                    }
                )

                VerticalDivider()

                if(card.can_freeze){
                    if(card.card_frozen_by_parent){
                        ActionItem(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Lock,
                            text = "UnFreeze",
                            color = Color(0xFFA57100),
                            onClick = {
                                // unfreeze card
                                viewModel.unFreezeCard(card.child_public_id)
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
                                viewModel.freezeCard(card.child_public_id)
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        // limit amount to spend
                        viewModel.openSetLimitDialog()
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
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "Set weekly limit",
                    color = Color(0xFF8D99AE),
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
            modifier = Modifier.size(22.dp)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = text,
            color = color,
            fontSize = 16.sp,
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


@Composable
fun AutoDismissSuccessDialog(
    showDialog: Boolean,
    message: String,
    onDismiss: () -> Unit,
    title: String = "Success",
    dismissDelay: Long = 3000L
) {

    // Auto dismiss effect
    LaunchedEffect(showDialog) {

        if (showDialog) {

            delay(dismissDelay)

            onDismiss()
        }
    }

    if (showDialog) {

        AlertDialog(

            onDismissRequest = {
                onDismiss()
            },

            icon = {

                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF2E7D32),
                    modifier = Modifier.size(52.dp)
                )
            },

            title = {

                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },

            text = {

                Text(
                    text = message,
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                )
            },

            confirmButton = {},

            shape = RoundedCornerShape(22.dp),

            containerColor = Color.White
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinkFamilyMemberDialog(
    showDialog: Boolean,

    phoneNumber: String,
    relationship: String,

    allowTopUp: Boolean,
    viewBalance: Boolean,
    viewHistory: Boolean,
    freezeCard: Boolean,

    relationshipOptions: List<String>,

    onPhoneNumberChange: (String) -> Unit,
    onRelationshipChange: (String) -> Unit,

    onAllowTopUpChange: (Boolean) -> Unit,
    onViewBalanceChange: (Boolean) -> Unit,
    onViewHistoryChange: (Boolean) -> Unit,
    onFreezeCardChange: (Boolean) -> Unit,

    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (!showDialog) return

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        containerColor = Color.White,

        confirmButton = {},

        title = {
            Text(
                text = "Link a Family Member",
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
                    "Their Phone Number",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = Color(0xFF374151)
                )

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = onPhoneNumberChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("e.g. 0771234567") },
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true
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
                    onCheckedChange = onViewBalanceChange
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
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
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
fun PermissionItem(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
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
        shape = RoundedCornerShape(22.dp),
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
                        viewModel.DeclineInvitation(invitations.relationship_id)
                    },
                    shape = RoundedCornerShape(14.dp),
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
                        viewModel.AcceptInvitation(invitations.relationship_id)
                    },
                    shape = RoundedCornerShape(14.dp),
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