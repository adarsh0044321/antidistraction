package com.antigravity.antidistraction.domain.model

data class FocusSessionState(
    val state: FocusState = FocusState.IDLE,
    val sessionId: Long = 0L,
    val profileId: Long = 1L,
    val mode: FocusMode = FocusMode.DEEP_FOCUS,
    val startTimeMs: Long = 0L,
    val targetDurationMs: Long = 25 * 60 * 1000L, // Default 25 min
    val remainingSeconds: Long = 25 * 60L,
    val progressFraction: Float = 1.0f,
    val totalInterventions: Int = 0,
    val totalBypasses: Int = 0
)
