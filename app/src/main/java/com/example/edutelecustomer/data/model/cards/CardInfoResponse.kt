package com.example.edutelecustomer.data.model.cards

data class CardInfoResponse(
    val card: CardInfo,
    val quick_stats: QuickStats,
    val recent_access_events: List<RecentRedemptions>,
    val spending_breakdown: List<UsageBreakdown>,
    val has_more_access_events: Boolean,
    val rewards: Rewards
)


data class CardInfo(
    val card_uid: String,
    val card_number: String,
    val remaining: String,
    val status: String,
    val issued_at: String,
    val expiry: String,
    val total_allocated: String,
    val total_spent: String,
    val total_refunds: String
)


data class QuickStats(
    val total_spent: StatItem,
    val access_events: StatItem,
    val pending: StatItem,
    val success_rate: StatItem
)

data class StatItem(
    val value: String,
    val change_pct: Double
)

data class RecentRedemptions(
    val activity: String,
    val place: String,
    val date: String,
    val amount: String,
    val status: String,
    val type: String
)

data class UsageBreakdown(
    val label: String,
    val amount: String,
    val percent: Int,
    val color: String
)

data class Rewards(
    val points_remaining: Int,
    val badge: Badge,
    val next_badge: NextBadge
)

data class Badge(
    val name: String,
    val slug: String,
    val points_required: Int
)

data class NextBadge(
    val name: String,
    val slug: String,
    val points_required: Int,
    val points_to_unlock: Int
)