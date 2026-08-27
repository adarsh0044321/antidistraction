package com.antigravity.antidistraction.domain.model

enum class BreakMode {
    POMODORO_25_5,
    LONG_CYCLE_50_10,
    DEEP_CYCLE_90_15,
    NO_BREAK
}

data class BreakState(
    val isBreakActive: Boolean = false,
    val breakMode: BreakMode = BreakMode.POMODORO_25_5,
    val breakDurationMinutes: Int = 5,
    val remainingBreakSeconds: Long = 5 * 60L,
    val completedWorkCycles: Int = 0
)
