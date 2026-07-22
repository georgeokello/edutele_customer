package com.eduteleaccess.customer.data.model.cards

data class ChildCardHistoryResponse (
    val items: List<ChildHistoryItems>,
    val total: Int
)

data class ChildHistoryItems(
    val public_id: String? = null,
    val tx_type: String? = null,
    val amount: String? = null,
    val status: String? = null,
    val description: String? = null,
    val created_at: String? = null,
)
