package com.adarshsingh.antidistraction.domain.analytics

import com.adarshsingh.antidistraction.data.local.dao.DistractionAttemptDao
import com.adarshsingh.antidistraction.data.local.dao.FocusSessionDao
import com.adarshsingh.antidistraction.domain.model.FocusAnalyticsSummary
import com.adarshsingh.antidistraction.domain.model.FocusState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsEngine @Inject constructor(
    private val sessionDao: FocusSessionDao,
    private val attemptDao: DistractionAttemptDao
) {

    fun getAnalyticsSummarySinceFlow(startTimeMs: Long): Flow<FocusAnalyticsSummary> {
        return sessionDao.getSessionsSinceFlow(startTimeMs).map { sessions ->
            val completed = sessions.count { it.state == FocusState.FOCUS_COMPLETED }
            val abandoned = sessions.count { it.state == FocusState.FOCUS_ABANDONED }
            val totalSessions = completed + abandoned

            val totalFocusTimeMs = sessions.filter { it.state == FocusState.FOCUS_COMPLETED }.sumOf {
                if (it.targetDurationMs > 0L) it.targetDurationMs else maxOf(0L, (it.actualEndTimeMs ?: it.startTimeMs) - it.startTimeMs)
            }
            val totalFocusTimeMins = totalFocusTimeMs / (1000 * 60)

            val totalInterventions = sessions.sumOf { it.totalInterventions }
            val totalBypasses = sessions.sumOf { it.totalBypasses }
            val resisted = maxOf(0, totalInterventions - totalBypasses)

            val rate = if (totalSessions > 0) (completed.toFloat() / totalSessions.toFloat() * 100).toInt() else 100

            FocusAnalyticsSummary(
                totalFocusTimeMinutes = totalFocusTimeMins,
                completedSessionsCount = completed,
                abandonedSessionsCount = abandoned,
                totalDistractionAttempts = totalInterventions,
                resistedAttemptsCount = resisted,
                totalBypassesCount = totalBypasses,
                completionRatePercentage = rate
            )
        }
    }
}
