package com.adarshsingh.antidistraction.domain.model

data class FocusAnalyticsSummary(
    val totalFocusTimeMinutes: Long,
    val completedSessionsCount: Int,
    val abandonedSessionsCount: Int,
    val totalDistractionAttempts: Int,
    val resistedAttemptsCount: Int,
    val totalBypassesCount: Int,
    val completionRatePercentage: Int
)
