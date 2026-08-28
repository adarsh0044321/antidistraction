package com.adarshsingh.antidistraction.domain.model

enum class RestrictionDecision {
    ALLOWED,
    EMERGENCY_ALLOWED,
    TEMPORARY_EXCEPTION_ALLOWED,
    WARN,
    DELAY,
    RESTRICTED
}

data class RestrictionResult(
    val packageName: String,
    val decision: RestrictionDecision,
    val reason: String,
    val recommendedInterventionLevel: InterventionLevel = InterventionLevel.LEVEL_0
)

data class TemporaryException(
    val packageName: String,
    val expirationTimestampMs: Long,
    val grantedReason: String
)
