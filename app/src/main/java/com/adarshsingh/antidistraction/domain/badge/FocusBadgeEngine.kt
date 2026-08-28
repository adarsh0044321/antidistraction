package com.adarshsingh.antidistraction.domain.badge

import com.adarshsingh.antidistraction.data.local.dao.FocusSessionDao
import com.adarshsingh.antidistraction.domain.analytics.AnalyticsEngine
import com.adarshsingh.antidistraction.domain.model.FocusState
import com.adarshsingh.antidistraction.domain.score.FocusScoreEngine
import kotlinx.coroutines.flow.first
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

data class FocusBadge(
    val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val isUnlocked: Boolean,
    val progressText: String
)

@Singleton
class FocusBadgeEngine @Inject constructor(
    private val sessionDao: FocusSessionDao,
    private val analyticsEngine: AnalyticsEngine,
    private val scoreEngine: FocusScoreEngine
) {

    suspend fun evaluateBadges(): List<FocusBadge> {
        val allSessions = sessionDao.getAllSessionsFlow().first()
        val completedSessions = allSessions.filter { it.state == FocusState.FOCUS_COMPLETED }
        val summary = analyticsEngine.getAnalyticsSummarySinceFlow(0L).first()
        val scoreDetails = scoreEngine.calculateFocusScore(summary)

        val totalCompleted = completedSessions.size
        val totalResisted = summary.resistedAttemptsCount
        val totalFocusMinutes = summary.totalFocusTimeMinutes

        // Check for early morning session (< 8 AM)
        val hasEarlySession = completedSessions.any { session ->
            val cal = Calendar.getInstance().apply { timeInMillis = session.startTimeMs }
            cal.get(Calendar.HOUR_OF_DAY) < 8
        }

        // Late night session check (21:00 to 02:00)
        val hasNightSession = completedSessions.any { session ->
            val cal = Calendar.getInstance().apply { timeInMillis = session.startTimeMs }
            val hour = cal.get(Calendar.HOUR_OF_DAY)
            hour >= 21 || hour < 2
        }

        // Long session check (>= 60 mins)
        val hasLongSession = completedSessions.any { session ->
            session.targetDurationMs >= 60 * 60 * 1000L
        }

        // Perfect session check (0 interventions)
        val hasPerfectSession = completedSessions.any { session ->
            session.totalInterventions == 0
        }

        // Consecutive streak calculation
        var currentStreak = 0
        for (session in allSessions.sortedByDescending { it.startTimeMs }) {
            if (session.state == FocusState.FOCUS_COMPLETED) {
                currentStreak++
            } else if (session.state == FocusState.FOCUS_ABANDONED) {
                break
            }
        }

        return listOf(
            FocusBadge(
                id = "FIRST_STEP",
                title = "First Step",
                description = "Complete your 1st focus session.",
                iconEmoji = "🧘",
                isUnlocked = totalCompleted >= 1,
                progressText = if (totalCompleted >= 1) "Unlocked!" else "$totalCompleted/1 completed"
            ),
            FocusBadge(
                id = "MINDFUL_PRACTICE",
                title = "Mindful Practice",
                description = "Complete a 25-minute Pomodoro focus session.",
                iconEmoji = "⏳",
                isUnlocked = completedSessions.any { it.targetDurationMs >= 25 * 60 * 1000L },
                progressText = if (completedSessions.any { it.targetDurationMs >= 25 * 60 * 1000L }) "Unlocked!" else "0/1 25m session"
            ),
            FocusBadge(
                id = "IRON_STREAK",
                title = "Iron Streak",
                description = "Complete 3 focus sessions without abandoning.",
                iconEmoji = "🔥",
                isUnlocked = currentStreak >= 3,
                progressText = if (currentStreak >= 3) "Unlocked! ($currentStreak streak)" else "$currentStreak/3 streak"
            ),
            FocusBadge(
                id = "STREAK_MASTER",
                title = "Streak Master",
                description = "Maintain a 7-session focus streak.",
                iconEmoji = "⚡",
                isUnlocked = currentStreak >= 7,
                progressText = if (currentStreak >= 7) "Unlocked!" else "$currentStreak/7 streak"
            ),
            FocusBadge(
                id = "ATTENTION_SHIELD",
                title = "Attention Shield",
                description = "Resist 5 distraction attempts.",
                iconEmoji = "🛡️",
                isUnlocked = totalResisted >= 5,
                progressText = if (totalResisted >= 5) "Unlocked!" else "$totalResisted/5 resisted"
            ),
            FocusBadge(
                id = "FORTRESS_OF_SOLITUDE",
                title = "Fortress of Solitude",
                description = "Resist 15 distraction attempts.",
                iconEmoji = "🏰",
                isUnlocked = totalResisted >= 15,
                progressText = if (totalResisted >= 15) "Unlocked!" else "$totalResisted/15 resisted"
            ),
            FocusBadge(
                id = "EARLY_BIRD",
                title = "Early Bird",
                description = "Complete a focus session before 8:00 AM.",
                iconEmoji = "🌅",
                isUnlocked = hasEarlySession,
                progressText = if (hasEarlySession) "Unlocked!" else "0/1 early focus"
            ),
            FocusBadge(
                id = "NIGHT_GUARDIAN",
                title = "Night Guardian",
                description = "Complete a focus session between 9 PM and 2 AM.",
                iconEmoji = "🦉",
                isUnlocked = hasNightSession,
                progressText = if (hasNightSession) "Unlocked!" else "0/1 night focus"
            ),
            FocusBadge(
                id = "DEEP_MIND",
                title = "Deep Mind",
                description = "Complete a 60+ minute Deep Focus session.",
                iconEmoji = "💎",
                isUnlocked = hasLongSession,
                progressText = if (hasLongSession) "Unlocked!" else "0/1 long session"
            ),
            FocusBadge(
                id = "ZERO_DISTRACTION",
                title = "Zero Distraction",
                description = "Complete a focus session with 0 interventions.",
                iconEmoji = "🎯",
                isUnlocked = hasPerfectSession,
                progressText = if (hasPerfectSession) "Unlocked!" else "0/1 zero-intervention session"
            ),
            FocusBadge(
                id = "HEAVY_LIFTER",
                title = "Heavy Lifter",
                description = "Accumulate 300+ minutes (5 hours) of total focus.",
                iconEmoji = "🏋️",
                isUnlocked = totalFocusMinutes >= 300,
                progressText = if (totalFocusMinutes >= 300) "Unlocked!" else "$totalFocusMinutes/300 mins"
            ),
            FocusBadge(
                id = "ATTENTION_MASTER",
                title = "Master of Focus",
                description = "Reach a 100-point Focus Score.",
                iconEmoji = "🏆",
                isUnlocked = scoreDetails.totalScore >= 100,
                progressText = if (scoreDetails.totalScore >= 100) "Unlocked!" else "${scoreDetails.totalScore}/100 score"
            )
        )
    }
}
