package com.adarshsingh.antidistraction.domain.brain

import com.adarshsingh.antidistraction.data.local.dao.BlockedAppDao
import com.adarshsingh.antidistraction.data.local.dao.DistractionAttemptDao
import com.adarshsingh.antidistraction.util.Logger
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DistractionBrain @Inject constructor(
    private val attemptDao: DistractionAttemptDao,
    private val blockedAppDao: BlockedAppDao
) {

    fun calculateDistractionScore(
        recentAttemptsCount: Int,
        totalAttempts: Int,
        bypassCount: Int,
        userClassification: String,
        hourOfDay: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    ): Int {
        var score = 0

        // Factor 1: Recent attempt frequency (Weight: 30)
        score += minOf(30, recentAttemptsCount * 10)

        // Factor 2: Total attempts volume (Weight: 20)
        score += minOf(20, totalAttempts * 2)

        // Factor 3: Bypass ratio penalty (Weight: 20)
        val bypassRatio = if (totalAttempts > 0) bypassCount.toFloat() / totalAttempts.toFloat() else 0f
        score += (bypassRatio * 20).toInt()

        // Factor 4: User classification offset (+20 for DISTRACTING, -20 for PRODUCTIVE)
        when (userClassification) {
            "DISTRACTING" -> score += 20
            "PRODUCTIVE" -> score -= 20
        }

        // Factor 5: Time-of-Day impulsivity weighting (+15 during late night 21:00 - 02:00)
        if (hourOfDay >= 21 || hourOfDay <= 2) {
            score += 15
        }

        val finalScore = score.coerceIn(0, 100)
        Logger.i("DistractionBrain", "Calculated Distraction Score: $finalScore (Attempts: $totalAttempts, Bypasses: $bypassCount, Hour: $hourOfDay)")
        return finalScore
    }

    suspend fun updateAppDistractionScore(packageName: String): Int {
        val fifteenMinsAgo = System.currentTimeMillis() - (15 * 60 * 1000L)
        val recentAttempts = attemptDao.getRecentAttemptCountForApp(packageName, fifteenMinsAgo)
        val savedApp = blockedAppDao.getAppByPackageName(packageName)

        val userClassification = savedApp?.userClassification ?: "NEUTRAL"
        val score = calculateDistractionScore(
            recentAttemptsCount = recentAttempts,
            totalAttempts = recentAttempts,
            bypassCount = 0,
            userClassification = userClassification
        )

        blockedAppDao.updateDistractionScore(packageName, score)
        return score
    }
}
