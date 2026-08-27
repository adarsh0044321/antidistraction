package com.antigravity.antidistraction.domain.model

data class AppDistractionMetrics(
    val packageName: String,
    val totalAttempts: Int,
    val recentAttemptsCount: Int,
    val bypassCount: Int,
    val returnToFocusCount: Int,
    val userClassification: String,
    val calculatedScore: Int
)
