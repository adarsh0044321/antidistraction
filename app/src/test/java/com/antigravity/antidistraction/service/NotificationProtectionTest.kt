package com.antigravity.antidistraction.service

import com.antigravity.antidistraction.data.local.entity.NotificationEventEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NotificationProtectionTest {

    @Test
    fun notificationEventEntity_defaultSuppressedIsTrue() {
        val event = NotificationEventEntity(
            packageName = "com.instagram.android",
            title = "New Message",
            text = "Hey check this out",
            postTimeMs = System.currentTimeMillis()
        )
        assertEquals("com.instagram.android", event.packageName)
        assertTrue(event.isSuppressed)
    }
}
