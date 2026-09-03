package com.adarshsingh.antidistraction.data.repository

import com.adarshsingh.antidistraction.data.local.dao.FocusProfileDao
import com.adarshsingh.antidistraction.data.local.dao.FocusSessionDao
import com.adarshsingh.antidistraction.data.local.entity.FocusSessionEntity
import com.adarshsingh.antidistraction.domain.model.FocusMode
import com.adarshsingh.antidistraction.domain.model.FocusState
import com.adarshsingh.antidistraction.domain.repository.FocusSessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FocusSessionRepositoryImpl @Inject constructor(
    private val sessionDao: FocusSessionDao,
    private val profileDao: FocusProfileDao
) : FocusSessionRepository {

    override suspend fun createSession(profileId: Long, durationMs: Long, mode: FocusMode): Long {
        val entity = FocusSessionEntity(
            profileId = profileId,
            focusMode = mode,
            startTimeMs = System.currentTimeMillis(),
            targetDurationMs = durationMs,
            state = FocusState.FOCUS_ACTIVE
        )
        return sessionDao.insertSession(entity)
    }

    override suspend fun updateSessionState(sessionId: Long, state: FocusState, actualEndTimeMs: Long?) {
        val session = sessionDao.getSessionById(sessionId) ?: return
        val finalEndTime = actualEndTimeMs ?: session.actualEndTimeMs
        val calculatedDuration = if (session.targetDurationMs == 0L && finalEndTime != null) {
            maxOf(0L, finalEndTime - session.startTimeMs)
        } else {
            session.targetDurationMs
        }

        val updated = session.copy(
            state = state,
            targetDurationMs = calculatedDuration,
            actualEndTimeMs = finalEndTime
        )
        sessionDao.updateSession(updated)
    }

    override suspend fun getSessionById(sessionId: Long): FocusSessionEntity? {
        return sessionDao.getSessionById(sessionId)
    }

    override suspend fun getActiveSession(): FocusSessionEntity? {
        val activeState = sessionDao.getLatestSessionWithState(FocusState.FOCUS_ACTIVE)
            ?: sessionDao.getLatestSessionWithState(FocusState.PAUSED)
            ?: sessionDao.getLatestSessionWithState(FocusState.RESUMED)
        return activeState
    }

    override fun getAllSessions(): Flow<List<FocusSessionEntity>> {
        return sessionDao.getAllSessionsFlow()
    }

    override suspend fun incrementInterventionCount(sessionId: Long) {
        sessionDao.incrementInterventionCount(sessionId)
    }
}
