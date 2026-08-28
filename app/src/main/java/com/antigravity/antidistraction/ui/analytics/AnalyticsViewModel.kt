package com.antigravity.antidistraction.ui.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antigravity.antidistraction.domain.analytics.AnalyticsEngine
import com.antigravity.antidistraction.domain.insights.InsightsEngine
import com.antigravity.antidistraction.domain.model.FocusAnalyticsSummary
import com.antigravity.antidistraction.domain.model.FocusInsight
import com.antigravity.antidistraction.domain.model.FocusScoreDetails
import com.antigravity.antidistraction.domain.score.FocusScoreEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

import com.antigravity.antidistraction.domain.badge.FocusBadge
import com.antigravity.antidistraction.domain.badge.FocusBadgeEngine

data class AnalyticsUiState(
    val summary: FocusAnalyticsSummary = FocusAnalyticsSummary(0, 0, 0, 0, 0, 0, 100),
    val scoreDetails: FocusScoreDetails = FocusScoreDetails(100, 40, 30, 30, "EXCELLENT"),
    val insights: List<FocusInsight> = emptyList(),
    val badges: List<FocusBadge> = emptyList()
)

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    analyticsEngine: AnalyticsEngine,
    private val insightsEngine: InsightsEngine,
    private val focusScoreEngine: FocusScoreEngine,
    private val badgeEngine: FocusBadgeEngine
) : ViewModel() {

    val uiState: StateFlow<AnalyticsUiState> = analyticsEngine.getAnalyticsSummarySinceFlow(
        startTimeMs = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L) // Last 7 days
    ).map { summary ->
        val score = focusScoreEngine.calculateFocusScore(summary)
        val insights = insightsEngine.generateInsights(summary)
        val badges = badgeEngine.evaluateBadges()
        AnalyticsUiState(
            summary = summary,
            scoreDetails = score,
            insights = insights,
            badges = badges
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = AnalyticsUiState()
    )
}
