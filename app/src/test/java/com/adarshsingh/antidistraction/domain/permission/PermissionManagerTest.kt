package com.adarshsingh.antidistraction.domain.permission

import org.junit.Assert.assertEquals
import org.junit.Test

class PermissionManagerTest {

    @Test
    fun protectionLevel_fullProtectionWhenAllGranted() {
        val state = PermissionState(
            isUsageAccessGranted = true,
            isAccessibilityGranted = true,
            isOverlayGranted = true,
            isNotificationGranted = true,
            accessibilityStatus = CapabilityStatus.GRANTED,
            usageStatus = CapabilityStatus.GRANTED,
            protectionLevel = ProtectionLevel.FULL_PROTECTION
        )
        assertEquals(ProtectionLevel.FULL_PROTECTION, state.protectionLevel)
        assertEquals(CapabilityStatus.GRANTED, state.accessibilityStatus)
    }

    @Test
    fun protectionLevel_limitedProtectionWhenPartial() {
        val state = PermissionState(
            isUsageAccessGranted = true,
            isAccessibilityGranted = false,
            isOverlayGranted = false,
            isNotificationGranted = true,
            accessibilityStatus = CapabilityStatus.RESTRICTED_BY_ANDROID,
            usageStatus = CapabilityStatus.GRANTED,
            protectionLevel = ProtectionLevel.LIMITED_PROTECTION
        )
        assertEquals(ProtectionLevel.LIMITED_PROTECTION, state.protectionLevel)
        assertEquals(CapabilityStatus.RESTRICTED_BY_ANDROID, state.accessibilityStatus)
    }

    @Test
    fun protectionLevel_noProtectionWhenNoneGranted() {
        val state = PermissionState(
            isUsageAccessGranted = false,
            isAccessibilityGranted = false,
            isOverlayGranted = false,
            isNotificationGranted = false,
            accessibilityStatus = CapabilityStatus.REVOKED,
            usageStatus = CapabilityStatus.DENIED,
            protectionLevel = ProtectionLevel.NO_PROTECTION
        )
        assertEquals(ProtectionLevel.NO_PROTECTION, state.protectionLevel)
        assertEquals(CapabilityStatus.REVOKED, state.accessibilityStatus)
    }
}
