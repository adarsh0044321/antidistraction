package com.antigravity.antidistraction.domain.model

enum class FocusState {
    IDLE,
    FOCUS_ACTIVE,
    PAUSED,
    RESUMED,
    WARNING,
    INTERVENTION,
    BYPASS_REQUESTED,
    TEMPORARY_BYPASS,
    EMERGENCY,
    FOCUS_COMPLETED,
    FOCUS_ABANDONED
}
