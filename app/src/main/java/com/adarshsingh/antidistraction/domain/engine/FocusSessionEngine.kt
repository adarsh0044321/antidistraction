package com.adarshsingh.antidistraction.domain.engine

import android.content.Context
import com.adarshsingh.antidistraction.data.preferences.UserPreferencesRepository
import com.adarshsingh.antidistraction.domain.model.FocusMode
import com.adarshsingh.antidistraction.domain.model.FocusSessionState
import com.adarshsingh.antidistraction.domain.model.FocusState
import com.adarshsingh.antidistraction.domain.repository.FocusSessionRepository
import com.adarshsingh.antidistraction.service.FocusForegroundService
import com.adarshsingh.antidistraction.util.Logger
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FocusSessionEngine @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sessionRepository: FocusSessionRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) {
    private val scope = CoroutineScope(Dispatchers.Default)

    private val _sessionState = MutableStateFlow(FocusSessionState())
    val sessionState: StateFlow<FocusSessionState> = _sessionState.asStateFlow()

    private var timerJob: Job? = null
    private var pausedTimeAccumulatedMs: Long = 0L
    private var pauseStartTimestampMs: Long = 0L

    init {
        recoverActiveSession()
    }

    suspend fun isSessionActive(): Boolean {
        val current = _sessionState.value.state
        if (current == FocusState.FOCUS_ACTIVE || current == FocusState.RESUMED) {
            return true
        }
        val activeSession = sessionRepository.getActiveSession()
        return activeSession != null && (activeSession.state == FocusState.FOCUS_ACTIVE || activeSession.state == FocusState.RESUMED)
    }

    fun startSession(durationMinutes: Int, mode: FocusMode = FocusMode.DEEP_FOCUS) {
        scope.launch {
            val durationMs = durationMinutes * 60 * 1000L
            val activeProfileId = 1L
            val sessionId = sessionRepository.createSession(activeProfileId, durationMs)

            val startTime = System.currentTimeMillis()
            pausedTimeAccumulatedMs = 0L

            val newState = FocusSessionState(
                state = FocusState.FOCUS_ACTIVE,
                sessionId = sessionId,
                profileId = activeProfileId,
                mode = mode,
                startTimeMs = startTime,
                targetDurationMs = durationMs,
                remainingSeconds = durationMinutes * 60L,
                progressFraction = 1.0f
            )

            _sessionState.value = newState
            sessionRepository.updateSessionState(sessionId, FocusState.FOCUS_ACTIVE)
            userPreferencesRepository.setCurrentFocusState(FocusState.FOCUS_ACTIVE)

            FocusForegroundService.startService(context)
            startTimerLoop()
            Logger.i("FocusSessionEngine", "Started session $sessionId for $durationMinutes mins ($mode)")
        }
    }

    fun recordIntervention() {
        val current = _sessionState.value
        if (current.sessionId != 0L && (current.state == FocusState.FOCUS_ACTIVE || current.state == FocusState.RESUMED)) {
            scope.launch {
                sessionRepository.incrementInterventionCount(current.sessionId)
            }
        }
    }

    fun pauseSession() {
        val current = _sessionState.value
        if (current.state == FocusState.FOCUS_ACTIVE || current.state == FocusState.RESUMED) {
            timerJob?.cancel()
            pauseStartTimestampMs = System.currentTimeMillis()
            val newState = current.copy(state = FocusState.PAUSED)
            _sessionState.value = newState

            scope.launch {
                sessionRepository.updateSessionState(current.sessionId, FocusState.PAUSED)
                userPreferencesRepository.setCurrentFocusState(FocusState.PAUSED)
            }
            Logger.i("FocusSessionEngine", "Paused session ${current.sessionId}")
        }
    }

    fun resumeSession() {
        val current = _sessionState.value
        if (current.state == FocusState.PAUSED) {
            val now = System.currentTimeMillis()
            pausedTimeAccumulatedMs += (now - pauseStartTimestampMs)

            val newState = current.copy(state = FocusState.RESUMED)
            _sessionState.value = newState

            scope.launch {
                sessionRepository.updateSessionState(current.sessionId, FocusState.RESUMED)
                userPreferencesRepository.setCurrentFocusState(FocusState.FOCUS_ACTIVE)
            }

            FocusForegroundService.startService(context)
            startTimerLoop()
            Logger.i("FocusSessionEngine", "Resumed session ${current.sessionId}")
        }
    }

    fun completeSession() {
        timerJob?.cancel()
        val current = _sessionState.value
        val now = System.currentTimeMillis()

        val newState = current.copy(
            state = FocusState.FOCUS_COMPLETED,
            remainingSeconds = 0L,
            progressFraction = 0.0f
        )
        _sessionState.value = newState

        FocusForegroundService.stopService(context)

        scope.launch {
            sessionRepository.updateSessionState(current.sessionId, FocusState.FOCUS_COMPLETED, actualEndTimeMs = now)
            userPreferencesRepository.setCurrentFocusState(FocusState.IDLE)
        }
        Logger.i("FocusSessionEngine", "Completed session ${current.sessionId}")
    }

    fun abandonSession() {
        timerJob?.cancel()
        val current = _sessionState.value
        val now = System.currentTimeMillis()

        val newState = FocusSessionState(state = FocusState.FOCUS_ABANDONED)
        _sessionState.value = newState

        FocusForegroundService.stopService(context)

        scope.launch {
            if (current.sessionId != 0L) {
                sessionRepository.updateSessionState(current.sessionId, FocusState.FOCUS_ABANDONED, actualEndTimeMs = now)
            }
            userPreferencesRepository.setCurrentFocusState(FocusState.IDLE)
        }
        Logger.i("FocusSessionEngine", "Abandoned session ${current.sessionId}")
    }

    private fun startTimerLoop() {
        timerJob?.cancel()
        timerJob = scope.launch {
            while (isActive) {
                updateTimerCalculation()
                delay(200L)
            }
        }
    }

    private fun updateTimerCalculation() {
        val current = _sessionState.value
        if (current.state != FocusState.FOCUS_ACTIVE && current.state != FocusState.RESUMED) return

        val now = System.currentTimeMillis()
        val totalElapsedMs = (now - current.startTimeMs) - pausedTimeAccumulatedMs
        val remainingMs = current.targetDurationMs - totalElapsedMs

        if (remainingMs <= 0) {
            completeSession()
        } else {
            val remainingSec = remainingMs / 1000L
            val progressFraction = remainingMs.toFloat() / current.targetDurationMs.toFloat()

            _sessionState.value = current.copy(
                remainingSeconds = remainingSec,
                progressFraction = progressFraction.coerceIn(0f, 1f)
            )
        }
    }

    fun recoverActiveSession() {
        scope.launch {
            val activeSession = sessionRepository.getActiveSession()
            if (activeSession != null) {
                val now = System.currentTimeMillis()
                val elapsed = now - activeSession.startTimeMs
                val remainingMs = activeSession.targetDurationMs - elapsed

                if (remainingMs <= 0) {
                    sessionRepository.updateSessionState(activeSession.id, FocusState.FOCUS_COMPLETED, actualEndTimeMs = now)
                    userPreferencesRepository.setCurrentFocusState(FocusState.IDLE)
                    _sessionState.value = FocusSessionState(state = FocusState.IDLE)
                } else {
                    val recoveredState = FocusSessionState(
                        state = activeSession.state,
                        sessionId = activeSession.id,
                        profileId = activeSession.profileId,
                        mode = activeSession.focusMode,
                        startTimeMs = activeSession.startTimeMs,
                        targetDurationMs = activeSession.targetDurationMs,
                        remainingSeconds = remainingMs / 1000L,
                        progressFraction = remainingMs.toFloat() / activeSession.targetDurationMs.toFloat()
                    )
                    _sessionState.value = recoveredState

                    if (activeSession.state == FocusState.FOCUS_ACTIVE || activeSession.state == FocusState.RESUMED) {
                        startTimerLoop()
                    }
                    Logger.i("FocusSessionEngine", "Recovered session ${activeSession.id} with ${remainingMs / 1000L}s remaining")
                }
            } else {
                _sessionState.value = FocusSessionState(state = FocusState.IDLE)
            }
        }
    }
}
