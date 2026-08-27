package com.antigravity.antidistraction.domain.model

enum class InsightType {
    BEST_FOCUS_TIME,
    TOP_DISTRACTOR,
    SESSION_LENGTH_TREND,
    COMPLETION_RATE
}

data class FocusInsight(
    val type: InsightType,
    val title: String,
    val description: String,
    val confidenceScore: Float = 1.0f
)
