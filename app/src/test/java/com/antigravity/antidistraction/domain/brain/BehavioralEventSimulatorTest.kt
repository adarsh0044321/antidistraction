package com.antigravity.antidistraction.domain.brain

import com.antigravity.antidistraction.domain.model.FocusAnalyticsSummary
import com.antigravity.antidistraction.domain.score.FocusScoreEngine
import org.junit.Assert.assertEquals
import org.junit.Test

class BehavioralEventSimulatorTest {

    @Test
    fun simulator_calculatesScoreFromSyntheticEvents() {
        val scoreEngine = FocusScoreEngine()
        val summary = FocusAnalyticsSummary(
            totalFocusTimeMinutes = 120L,
            completedSessionsCount = 4,
            abandonedSessionsCount = 0,
            totalDistractionAttempts = 5,
            resistedAttemptsCount = 5,
            totalBypassesCount = 0,
            completionRatePercentage = 100
        )
        val score = scoreEngine.calculateFocusScore(summary)

        assertEquals("EXCELLENT", score.scoreGrade)
        assertEquals(100, score.totalScore)
    }
}
