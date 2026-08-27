package com.antigravity.antidistraction.domain.engine

import com.antigravity.antidistraction.data.local.dao.DistractionAttemptDao
import com.antigravity.antidistraction.domain.model.FocusMode
import com.antigravity.antidistraction.domain.model.InterventionLevel
import com.antigravity.antidistraction.util.Logger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EscalationEngine @Inject constructor(
    private val attemptDao: DistractionAttemptDao
) {
    suspend fun calculateEscalatedLevel(
        packageName: String,
        baseLevel: InterventionLevel,
        mode: FocusMode
    ): InterventionLevel {
        val fifteenMinsAgo = System.currentTimeMillis() - (15 * 60 * 1000L)
        val recentAttemptsCount = attemptDao.getRecentAttemptCountForApp(packageName, fifteenMinsAgo)

        val levelByAttempts = when {
            recentAttemptsCount >= 5 -> InterventionLevel.LEVEL_6
            recentAttemptsCount >= 3 -> InterventionLevel.LEVEL_5
            recentAttemptsCount >= 2 -> InterventionLevel.LEVEL_4
            recentAttemptsCount == 1 -> maxOfLevel(baseLevel, InterventionLevel.LEVEL_3)
            else -> baseLevel
        }

        // Deep Focus profile enforces minimum Level 4 friction
        val finalLevel = if (mode == FocusMode.DEEP_FOCUS) {
            maxOfLevel(levelByAttempts, InterventionLevel.LEVEL_4)
        } else {
            levelByAttempts
        }

        Logger.i("EscalationEngine", "Package $packageName attempts in last 15m: $recentAttemptsCount ($mode) -> Level ${finalLevel.name}")
        return finalLevel
    }

    private fun maxOfLevel(a: InterventionLevel, b: InterventionLevel): InterventionLevel {
        return if (a.severity >= b.severity) a else b
    }
}
