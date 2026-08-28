package com.adarshsingh.antidistraction.domain.engine

import com.adarshsingh.antidistraction.data.local.dao.BlockedAppDao
import com.adarshsingh.antidistraction.data.local.dao.DistractionAttemptDao
import com.adarshsingh.antidistraction.data.local.entity.DistractionAttemptEntity
import com.adarshsingh.antidistraction.domain.model.IntentionType
import com.adarshsingh.antidistraction.domain.model.InterventionLevel
import com.adarshsingh.antidistraction.domain.model.RestrictionResult
import com.adarshsingh.antidistraction.util.Logger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InterventionEngine @Inject constructor(
    private val escalationEngine: EscalationEngine,
    private val deEscalationEngine: DeEscalationEngine,
    private val restrictionEngine: RestrictionEngine,
    private val attemptDao: DistractionAttemptDao,
    private val blockedAppDao: BlockedAppDao,
    private val sessionEngine: FocusSessionEngine
) {

    suspend fun processIntervention(packageName: String): RestrictionResult {
        val baseResult = restrictionEngine.evaluateAppAccess(packageName)
        val sessionState = sessionEngine.sessionState.value

        if (baseResult.recommendedInterventionLevel == InterventionLevel.LEVEL_0) {
            return baseResult
        }

        // Escalation calculation
        val escalatedLevel = escalationEngine.calculateEscalatedLevel(
            packageName = packageName,
            baseLevel = baseResult.recommendedInterventionLevel,
            mode = sessionState.mode
        )

        // De-escalation adaptation check
        val savedApp = blockedAppDao.getAppByPackageName(packageName)
        val finalLevel = deEscalationEngine.deEscalateLevel(
            currentLevel = escalatedLevel,
            recentIntentions = emptyList(),
            userClassification = savedApp?.userClassification ?: "NEUTRAL"
        )

        Logger.i("InterventionEngine", "Intervention level evaluated for $packageName: ${finalLevel.name}")
        return baseResult.copy(recommendedInterventionLevel = finalLevel)
    }

    suspend fun recordAttempt(
        packageName: String,
        intention: IntentionType?,
        level: InterventionLevel,
        bypassGranted: Boolean,
        userAction: String
    ) {
        val currentSessionId = sessionEngine.sessionState.value.sessionId
        val entity = DistractionAttemptEntity(
            sessionId = if (currentSessionId != 0L) currentSessionId else null,
            packageName = packageName,
            timestampMs = System.currentTimeMillis(),
            intentionChoice = intention,
            interventionLevelApplied = level,
            bypassGranted = bypassGranted,
            userActionTaken = userAction
        )
        attemptDao.insertAttempt(entity)

        if (bypassGranted) {
            restrictionEngine.grantTemporaryException(packageName, 2, intention?.displayName ?: "User Bypass")
        }

        Logger.i("InterventionEngine", "Recorded attempt for $packageName: Action = $userAction, Bypass = $bypassGranted")
    }
}
