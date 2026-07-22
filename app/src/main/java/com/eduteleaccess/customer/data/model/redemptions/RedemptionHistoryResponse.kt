package com.eduteleaccess.customer.data.model.redemptions


data class RedemptionHistoryResponse(
    val items: List<RedemptionItem>,
    val total: Int
)

data class RedemptionItem(
    val public_id: String,
    val type: String,
    val status: String,
    val amount: String,
    val remaining_before: String,
    val remaining_after: String,
    val reference: String,
    val remarks: String,
    val timestamp: String,
    val verified_at: String,

)