package com.adarshsingh.antidistraction.domain.antibypass

import com.adarshsingh.antidistraction.domain.model.ProtectionRecoveryState
import com.adarshsingh.antidistraction.domain.permission.PermissionManager
import com.adarshsingh.antidistraction.domain.permission.ProtectionLevel
import com.adarshsingh.antidistraction.util.Logger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AntiBypassMonitor @Inject constructor(
    private val permissionManager: PermissionManager
) {

    fun auditProtectionIntegrity(): ProtectionRecoveryState {
        val permissionState = permissionManager.getPermissionState()
        val revoked = mutableListOf<String>()

        if (!permissionState.isAccessibilityGranted) revoked.add("Accessibility Service")
        if (!permissionState.isUsageAccessGranted) revoked.add("Usage Access")
        if (!permissionState.isOverlayGranted) revoked.add("Display Over Apps")

        val isIntact = permissionState.protectionLevel == ProtectionLevel.FULL_PROTECTION

        if (!isIntact) {
            Logger.w("AntiBypassMonitor", "Protection compromised! Revoked permissions: ${revoked.joinToString()}")
        }

        return ProtectionRecoveryState(
            isProtectionIntact = isIntact,
            isServiceRecovered = true,
            revokedPermissions = revoked,
            recoveryMessage = if (!isIntact) "Protection capability degraded. Revoked: ${revoked.joinToString()}" else null
        )
    }
}
