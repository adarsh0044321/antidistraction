package com.antigravity.antidistraction.domain.model

data class ProtectionRecoveryState(
    val isProtectionIntact: Boolean,
    val isServiceRecovered: Boolean,
    val revokedPermissions: List<String> = emptyList(),
    val recoveryMessage: String? = null
)
