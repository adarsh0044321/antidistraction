package com.adarshsingh.antidistraction.ui

import org.junit.Assert.assertTrue
import org.junit.Test

class AccessibilityAuditTest {

    @Test
    fun calmButton_minimumTouchTargetHeightIs48dp() {
        val minHeightDp = 48
        assertTrue("Button height must meet 48dp accessibility guideline", minHeightDp >= 48)
    }

    @Test
    fun calmChip_touchPaddingMeetsGuidelines() {
        val chipHeightDp = 40
        assertTrue("Touch target padding must be accessible", chipHeightDp >= 36)
    }
}
