package com.adarshsingh.antidistraction.domain.compatibility

import org.junit.Assert.assertNotNull
import org.junit.Test

class OemCompatibilityManagerTest {

    @Test
    fun detectOemGuide_returnsNonNullGuide() {
        val manager = OemCompatibilityManager()
        val guide = manager.detectOemGuide()
        assertNotNull(guide.manufacturerName)
        assertNotNull(guide.setupInstructions)
    }
}
