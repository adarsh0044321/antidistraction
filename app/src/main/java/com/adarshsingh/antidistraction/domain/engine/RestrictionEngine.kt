package com.adarshsingh.antidistraction.domain.engine

import com.adarshsingh.antidistraction.domain.model.FocusMode
import com.adarshsingh.antidistraction.domain.model.FocusState
import com.adarshsingh.antidistraction.domain.model.InterventionLevel
import com.adarshsingh.antidistraction.domain.model.RestrictionDecision
import com.adarshsingh.antidistraction.domain.model.RestrictionResult
import com.adarshsingh.antidistraction.domain.model.TemporaryException
import com.adarshsingh.antidistraction.domain.repository.AppRestrictionRepository
import com.adarshsingh.antidistraction.util.Logger
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RestrictionEngine @Inject constructor(
    private val appRestrictionRepository: AppRestrictionRepository,
    private val sessionEngine: FocusSessionEngine
) {
    private val activeTemporaryExceptions = ConcurrentHashMap<String, TemporaryException>()

    suspend fun evaluateAppAccess(packageName: String): RestrictionResult {
        // Rule 1: Emergency apps are always allowed
        if (appRestrictionRepository.isAppEmergency(packageName)) {
            return RestrictionResult(
                packageName = packageName,
                decision = RestrictionDecision.EMERGENCY_ALLOWED,
                reason = "Emergency application access permitted.",
                recommendedInterventionLevel = InterventionLevel.LEVEL_0
            )
        }

        // Rule 2: Active valid temporary exceptions bypass restriction
        val activeException = activeTemporaryExceptions[packageName]
        if (activeException != null) {
            if (System.currentTimeMillis() < activeException.expirationTimestampMs) {
                val remainingMins = (activeException.expirationTimestampMs - System.currentTimeMillis()) / (60 * 1000L) + 1
                return RestrictionResult(
                    packageName = packageName,
                    decision = RestrictionDecision.TEMPORARY_EXCEPTION_ALLOWED,
                    reason = "Temporary access granted ($remainingMins mins remaining).",
                    recommendedInterventionLevel = InterventionLevel.LEVEL_0
                )
            } else {
                activeTemporaryExceptions.remove(packageName)
                Logger.i("RestrictionEngine", "Temporary exception expired for $packageName")
            }
        }

        // Rule 3: Evaluate current session state
        val sessionState = sessionEngine.sessionState.value
        if (sessionState.state != FocusState.FOCUS_ACTIVE && sessionState.state != FocusState.RESUMED) {
            return RestrictionResult(
                packageName = packageName,
                decision = RestrictionDecision.ALLOWED,
                reason = "No active focus session.",
                recommendedInterventionLevel = InterventionLevel.LEVEL_0
            )
        }

        // Rule 4: If an app is explicitly allowed by the user, permit access
        val isExplicitlyRestricted = appRestrictionRepository.isAppRestricted(packageName)
        if (!isExplicitlyRestricted) {
            return RestrictionResult(
                packageName = packageName,
                decision = RestrictionDecision.ALLOWED,
                reason = "Application is in allowed list.",
                recommendedInterventionLevel = InterventionLevel.LEVEL_0
            )
        }

        // Rule 5: Evaluate profile-specific friction levels for restricted apps
        return when (sessionState.mode) {
            FocusMode.DEEP_FOCUS -> RestrictionResult(
                packageName = packageName,
                decision = RestrictionDecision.RESTRICTED,
                reason = "Deep Focus profile restricts this application.",
                recommendedInterventionLevel = InterventionLevel.LEVEL_5
            )
            FocusMode.STUDY -> RestrictionResult(
                packageName = packageName,
                decision = RestrictionDecision.RESTRICTED,
                reason = "Study profile restricts distracting apps.",
                recommendedInterventionLevel = InterventionLevel.LEVEL_3
            )
            FocusMode.WORK -> RestrictionResult(
                packageName = packageName,
                decision = RestrictionDecision.WARN,
                reason = "Work profile warning for distracting apps.",
                recommendedInterventionLevel = InterventionLevel.LEVEL_2
            )
            FocusMode.LIGHT_FOCUS -> RestrictionResult(
                packageName = packageName,
                decision = RestrictionDecision.WARN,
                reason = "Light focus warning.",
                recommendedInterventionLevel = InterventionLevel.LEVEL_1
            )
            FocusMode.CUSTOM -> RestrictionResult(
                packageName = packageName,
                decision = RestrictionDecision.RESTRICTED,
                reason = "Custom rule restriction.",
                recommendedInterventionLevel = InterventionLevel.LEVEL_4
            )
        }
    }

    fun grantTemporaryException(packageName: String, durationMinutes: Int, reason: String) {
        val expiration = System.currentTimeMillis() + (durationMinutes * 60 * 1000L)
        val exception = TemporaryException(packageName, expiration, reason)
        activeTemporaryExceptions[packageName] = exception
        Logger.i("RestrictionEngine", "Granted temporary exception for $packageName ($durationMinutes mins)")
    }

    fun revokeTemporaryException(packageName: String) {
        activeTemporaryExceptions.remove(packageName)
    }

    fun getActiveExceptions(): List<TemporaryException> {
        return activeTemporaryExceptions.values.filter { System.currentTimeMillis() < it.expirationTimestampMs }
    }
}
