package com.antigravity.antidistraction.domain.antibypass

import android.content.Context
import com.antigravity.antidistraction.domain.permission.CapabilityStatus
import com.antigravity.antidistraction.domain.permission.PermissionManager
import com.antigravity.antidistraction.domain.permission.PermissionState
import com.antigravity.antidistraction.domain.permission.ProtectionLevel
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FakePermissionManager(private val stateToReturn: PermissionState) : PermissionManager(
    ContextWrapperStub()
) {
    override fun getPermissionState(): PermissionState = stateToReturn
}

private class ContextWrapperStub : android.content.ContextWrapper(null)

class AntiBypassMonitorTest {

    @Test
    fun auditProtectionIntegrity_reportsIntactWhenFullProtection() {
        val state = PermissionState(
            isUsageAccessGranted = true,
            isAccessibilityGranted = true,
            isOverlayGranted = true,
            isNotificationGranted = true,
            accessibilityStatus = CapabilityStatus.GRANTED,
            usageStatus = CapabilityStatus.GRANTED,
            protectionLevel = ProtectionLevel.FULL_PROTECTION
        )

        val monitor = AntiBypassMonitor(FakePermissionManager(state))
        val result = monitor.auditProtectionIntegrity()
        assertTrue(result.isProtectionIntact)
    }

    @Test
    fun auditProtectionIntegrity_reportsCompromisedWhenPermissionRevoked() {
        val state = PermissionState(
            isUsageAccessGranted = true,
            isAccessibilityGranted = false,
            isOverlayGranted = true,
            isNotificationGranted = true,
            accessibilityStatus = CapabilityStatus.REVOKED,
            usageStatus = CapabilityStatus.GRANTED,
            protectionLevel = ProtectionLevel.LIMITED_PROTECTION
        )

        val monitor = AntiBypassMonitor(FakePermissionManager(state))
        val result = monitor.auditProtectionIntegrity()
        assertFalse(result.isProtectionIntact)
        assertTrue(result.revokedPermissions.contains("Accessibility Service"))
    }
}
