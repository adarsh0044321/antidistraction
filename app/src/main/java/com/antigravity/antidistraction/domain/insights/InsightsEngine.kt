package com.antigravity.antidistraction.domain.insights

import com.antigravity.antidistraction.domain.model.FocusAnalyticsSummary
import com.antigravity.antidistraction.domain.model.FocusInsight
import com.antigravity.antidistraction.domain.model.InsightType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InsightsEngine @Inject constructor() {

    fun generateInsights(summary: FocusAnalyticsSummary): List<FocusInsight> {
        val insights = mutableListOf<FocusInsight>()

        // Insight 1: Completion Rate Observation
        if (summary.completedSessionsCount + summary.abandonedSessionsCount >= 3) {
            if (summary.completionRatePercentage >= 80) {
                insights.add(
                    FocusInsight(
                        type = InsightType.COMPLETION_RATE,
                        title = "High Focus Consistency",
                        description = "You completed ${summary.completionRatePercentage}% of your scheduled focus sessions this week."
                    )
                )
            } else {
                insights.add(
                    FocusInsight(
                        type = InsightType.COMPLETION_RATE,
                        title = "Session Abandonment Trend",
                        description = "Consider shortening your session target duration from 45 minutes to 25 minutes to build momentum."
                    )
                )
            }
        }

        // Insight 2: Distraction Resistance Observation
        if (summary.totalDistractionAttempts >= 5) {
            insights.add(
                FocusInsight(
                    type = InsightType.TOP_DISTRACTOR,
                    title = "Distraction Resistance",
                    description = "You successfully resisted ${summary.resistedAttemptsCount} out of ${summary.totalDistractionAttempts} distraction attempts today."
                )
            )
        } else {
            insights.add(
                FocusInsight(
                    type = InsightType.BEST_FOCUS_TIME,
                    title = "Optimal Focus Window",
                    description = "Your strongest focus period this week is between 8:00 AM and 11:00 AM."
                )
            )
        }

        return insights
    }
}
