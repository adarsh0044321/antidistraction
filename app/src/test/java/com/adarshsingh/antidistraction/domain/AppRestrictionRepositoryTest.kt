package com.adarshsingh.antidistraction.domain

import com.adarshsingh.antidistraction.domain.model.AppInfo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppRestrictionRepositoryTest {

    @Test
    fun appInfo_defaultValuesAreCorrect() {
        val app = AppInfo(
            packageName = "com.instagram.android",
            appName = "Instagram",
            category = "Social Media"
        )
        assertEquals("Instagram", app.appName)
        assertEquals("Social Media", app.category)
        assertFalse(app.isBlocked)
        assertFalse(app.isEmergencyApp)
    }

    @Test
    fun appInfo_emergencyStateOverridesRestriction() {
        val emergencyApp = AppInfo(
            packageName = "com.google.android.dialer",
            appName = "Phone",
            category = "Utilities",
            isBlocked = false,
            isEmergencyApp = true
        )
        assertTrue(emergencyApp.isEmergencyApp)
        assertFalse(emergencyApp.isBlocked)
    }
}
