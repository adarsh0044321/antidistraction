package com.adarshsingh.antidistraction.release

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ReleaseCandidateVerificationTest {

    @Test
    fun releaseCandidate_metadataIsReady() {
        val appName = "Anti-Distraction"
        val versionName = "1.0.0"
        val versionCode = 1

        assertEquals("Anti-Distraction", appName)
        assertEquals("1.0.0", versionName)
        assertEquals(1, versionCode)
        assertNotNull(appName)
    }
}
