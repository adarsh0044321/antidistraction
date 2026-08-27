package com.antigravity.antidistraction.domain.score

import com.antigravity.antidistraction.domain.model.FocusAnalyticsSummary
import org.junit.Assert.assertEquals
import org.junit.Test

class FocusScoreEngineTest {

    @Test
    fun calculateFocusScore_perfectScoreReturns100() {
        val engine = FocusScoreEngine()
        val summary = FocusAnalyticsSummary(
            totalFocusTimeMinutes = 120,
            completedSessionsCount = 4,
            abandonedSessionsCount = 0,
            totalDistractionAttempts = 10,
            resistedAttemptsCount = 10,
            totalBypassesCount = 0,
            completionRatePercentage = 100
        )
        val score = engine.calculateFocusScore(summary)
        assertEquals(100, score.totalScore)
        assertEquals("EXCELLENT", score.scoreGrade)
    }
}
