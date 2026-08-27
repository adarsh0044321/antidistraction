package com.antigravity.antidistraction.domain.score

import com.antigravity.antidistraction.domain.model.FocusAnalyticsSummary
import com.antigravity.antidistraction.domain.model.FocusScoreDetails
import com.antigravity.antidistraction.util.Logger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FocusScoreEngine @Inject constructor() {

    fun calculateFocusScore(summary: FocusAnalyticsSummary): FocusScoreDetails {
        // Component 1: Completion Rate (Max 40 points)
        val completionScore = (summary.completionRatePercentage * 0.40f).toInt().coerceIn(0, 40)

        // Component 2: Focus Duration (Max 30 points, target 120 mins/day)
        val durationRatio = (summary.totalFocusTimeMinutes.toFloat() / 120f).coerceIn(0f, 1f)
        val durationScore = (durationRatio * 30f).toInt().coerceIn(0, 30)

        // Component 3: Resistance Rate (Max 30 points)
        val resistanceRatio = if (summary.totalDistractionAttempts > 0) {
            summary.resistedAttemptsCount.toFloat() / summary.totalDistractionAttempts.toFloat()
        } else {
            1.0f
        }
        val resistanceScore = (resistanceRatio * 30f).toInt().coerceIn(0, 30)

        val totalScore = (completionScore + durationScore + resistanceScore).coerceIn(0, 100)

        val grade = when {
            totalScore >= 85 -> "EXCELLENT"
            totalScore >= 70 -> "STRONG"
            totalScore >= 50 -> "MODERATE"
            else -> "BUILDING"
        }

        Logger.i("FocusScoreEngine", "Focus Score: $totalScore ($grade) [Comp: $completionScore, Dur: $durationScore, Res: $resistanceScore]")

        return FocusScoreDetails(
            totalScore = totalScore,
            completionComponent = completionScore,
            durationComponent = durationScore,
            resistanceComponent = resistanceScore,
            scoreGrade = grade
        )
    }
}
