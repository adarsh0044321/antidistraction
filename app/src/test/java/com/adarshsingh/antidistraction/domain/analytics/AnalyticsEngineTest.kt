package com.adarshsingh.antidistraction.domain.analytics

import com.adarshsingh.antidistraction.domain.model.FocusAnalyticsSummary
import org.junit.Assert.assertEquals
import org.junit.Test

class AnalyticsEngineTest {

    @Test
    fun focusAnalyticsSummary_completionRateCalculatedCorrectly() {
        val summary = FocusAnalyticsSummary(
            totalFocusTimeMinutes = 120,
            completedSessionsCount = 4,
            abandonedSessionsCount = 1,
            totalDistractionAttempts = 10,
            resistedAttemptsCount = 8,
            totalBypassesCount = 2,
            completionRatePercentage = 80
        )
        assertEquals(80, summary.completionRatePercentage)
        assertEquals(120L, summary.totalFocusTimeMinutes)
    }
}
