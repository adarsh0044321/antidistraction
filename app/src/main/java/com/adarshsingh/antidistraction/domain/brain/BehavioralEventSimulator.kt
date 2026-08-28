package com.adarshsingh.antidistraction.domain.brain

import com.adarshsingh.antidistraction.domain.model.FocusAnalyticsSummary
import com.adarshsingh.antidistraction.domain.model.FocusScoreDetails
import com.adarshsingh.antidistraction.domain.score.FocusScoreEngine
import javax.inject.Inject
import javax.inject.Singleton

sealed class BehavioralEvent {
    data class AppOpened(val packageName: String, val timestampMs: Long) : BehavioralEvent()
    data class AttemptResisted(val packageName: String, val timestampMs: Long) : BehavioralEvent()
    data class BypassGranted(val packageName: String, val timestampMs: Long) : BehavioralEvent()
}

@Singleton
class BehavioralEventSimulator @Inject constructor(
    private val distractionBrain: DistractionBrain,
    private val focusScoreEngine: FocusScoreEngine
) {

    fun simulateSessionEvents(events: List<BehavioralEvent>): FocusScoreDetails {
        var resisted = 0
        var bypassed = 0

        events.forEach { event ->
            when (event) {
                is BehavioralEvent.AttemptResisted -> resisted++
                is BehavioralEvent.BypassGranted -> bypassed++
                else -> {}
            }
        }

        val total = resisted + bypassed
        val summary = FocusAnalyticsSummary(
            totalFocusTimeMinutes = 60L,
            completedSessionsCount = 2,
            abandonedSessionsCount = 0,
            totalDistractionAttempts = total,
            resistedAttemptsCount = resisted,
            totalBypassesCount = bypassed,
            completionRatePercentage = 100
        )

        return focusScoreEngine.calculateFocusScore(summary)
    }
}
