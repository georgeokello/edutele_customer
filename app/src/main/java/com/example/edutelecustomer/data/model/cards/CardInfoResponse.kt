package com.example.edutelecustomer.data.model.cards

data class CardInfoResponse(
    val card: CardInfo,
    val quick_stats: QuickStats,
    val recent_transactions: List<RecentTransaction>,
    val spending_breakdown: List<SpendingBreakdown>
)

data class CardInfo(
    val card_uid: String,
    val card_number: String,
    val balance: String,
    val status: String,
    val issued_at: String,
    val expiry: String,
    val total_topups: String,
    val total_spent: String,
    val total_refunds: String
)


data class QuickStats(
    val total_spent: StatItem,
    val transactions: StatItem,
    val pending: StatItem,
    val success_rate: StatItem
)

data class StatItem(
    val value: String,
    val change_pct: Double
)

data class RecentTransaction(
    val activity: String,
    val place: String,
    val date: String,
    val amount: String,
    val status: String,
    val type: String
)

data class SpendingBreakdown(
    val label: String,
    val amount: String,
    val percent: Int,
    val color: String
)