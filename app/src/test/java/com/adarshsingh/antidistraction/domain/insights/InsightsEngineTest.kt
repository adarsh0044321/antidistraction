package com.adarshsingh.antidistraction.domain.insights

import com.adarshsingh.antidistraction.domain.model.FocusAnalyticsSummary
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class InsightsEngineTest {

    @Test
    fun generateInsights_returnsFactualInsights() {
        val engine = InsightsEngine()
        val summary = FocusAnalyticsSummary(
            totalFocusTimeMinutes = 150,
            completedSessionsCount = 5,
            abandonedSessionsCount = 0,
            totalDistractionAttempts = 8,
            resistedAttemptsCount = 7,
            totalBypassesCount = 1,
            completionRatePercentage = 100
        )
        val insights = engine.generateInsights(summary)
        assertTrue(insights.isNotEmpty())
        assertEquals("High Focus Consistency", insights[0].title)
    }
}
